#!/usr/bin/env python3
from __future__ import annotations

import argparse
import struct
import zipfile


def u16(data: bytes, off: int) -> int:
    return struct.unpack_from("<H", data, off)[0]


def u32(data: bytes, off: int) -> int:
    return struct.unpack_from("<I", data, off)[0]


def uleb(data: bytes, off: int) -> tuple[int, int]:
    value = 0
    shift = 0
    while True:
        b = data[off]
        off += 1
        value |= (b & 0x7F) << shift
        if not (b & 0x80):
            return value, off
        shift += 7


def class_methods(data: bytes, target: str) -> list[str]:
    string_ids_size = u32(data, 0x38)
    string_ids_off = u32(data, 0x3C)
    type_ids_size = u32(data, 0x40)
    type_ids_off = u32(data, 0x44)
    proto_ids_size = u32(data, 0x48)
    proto_ids_off = u32(data, 0x4C)
    method_ids_size = u32(data, 0x58)
    method_ids_off = u32(data, 0x5C)
    class_defs_size = u32(data, 0x60)
    class_defs_off = u32(data, 0x64)

    strings: dict[int, str] = {}

    def get_string(idx: int) -> str:
        if idx in strings:
            return strings[idx]
        off = u32(data, string_ids_off + idx * 4)
        _, pos = uleb(data, off)
        end = data.index(b"\x00", pos)
        value = data[pos:end].decode("utf-8", errors="replace")
        strings[idx] = value
        return value

    def get_type(idx: int) -> str:
        return get_string(u32(data, type_ids_off + idx * 4))

    def get_proto(idx: int) -> str:
        off = proto_ids_off + idx * 12
        return_type_idx = u32(data, off + 4)
        params_off = u32(data, off + 8)
        params: list[str] = []
        if params_off:
            size = u32(data, params_off)
            for i in range(size):
                params.append(get_type(u16(data, params_off + 4 + i * 2)))
        return "(" + "".join(params) + ")" + get_type(return_type_idx)

    method_ids = []
    for idx in range(method_ids_size):
        off = method_ids_off + idx * 8
        class_idx = u16(data, off)
        proto_idx = u16(data, off + 2)
        name_idx = u32(data, off + 4)
        method_ids.append((get_type(class_idx), get_string(name_idx), get_proto(proto_idx)))

    target_data_off = None
    for idx in range(class_defs_size):
        off = class_defs_off + idx * 32
        class_idx = u32(data, off)
        if get_type(class_idx) == target:
            target_data_off = u32(data, off + 24)
            break

    if not target_data_off:
        return []

    pos = target_data_off
    static_fields, pos = uleb(data, pos)
    instance_fields, pos = uleb(data, pos)
    direct_methods, pos = uleb(data, pos)
    virtual_methods, pos = uleb(data, pos)

    for _ in range(static_fields + instance_fields):
        _, pos = uleb(data, pos)
        _, pos = uleb(data, pos)

    out: list[str] = []

    def read_methods(count: int, kind: str, pos: int) -> int:
        method_idx = 0
        for _ in range(count):
            diff, pos = uleb(data, pos)
            access, pos = uleb(data, pos)
            code_off, pos = uleb(data, pos)
            method_idx += diff
            owner, name, proto = method_ids[method_idx]
            out.append(
                f"{kind:7} access=0x{access:x} code=0x{code_off:x} "
                f"{owner}->{name}{proto}"
            )
        return pos

    pos = read_methods(direct_methods, "direct", pos)
    read_methods(virtual_methods, "virtual", pos)
    return out


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("apk")
    parser.add_argument("descriptor")
    args = parser.parse_args()

    found = False
    with zipfile.ZipFile(args.apk) as zf:
        for name in sorted(n for n in zf.namelist() if n.startswith("classes") and n.endswith(".dex")):
            methods = class_methods(zf.read(name), args.descriptor)
            if methods:
                found = True
                print(f"## {name}")
                for method in methods:
                    print(method)

    if not found:
        print(f"No class data/methods found for {args.descriptor}")
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
