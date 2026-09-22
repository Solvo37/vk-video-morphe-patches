#!/usr/bin/env python3
"""Static release gate for a patched VK Video APK."""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import re
import sys
import zipfile

ORIGINAL_NATIVE_PATTERN = bytes.fromhex("1f1c0072e80700f900050054")
PATCHED_NATIVE_PATTERN = bytes.fromhex("1f1c0072e80700f900040054")
APPLICATION_DESCRIPTOR = b"Lcom/vk/video/app/VkVideoApplication;"
V1_SIGNATURE_RE = re.compile(r"^META-INF/(?:MANIFEST\.MF|[^/]+\.(?:SF|RSA|DSA|EC))$", re.I)


def count_pattern(data: bytes, pattern: bytes) -> int:
    count = 0
    start = 0
    while True:
        idx = data.find(pattern, start)
        if idx < 0:
            return count
        count += 1
        start = idx + 1


def sha256(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as fh:
        for chunk in iter(lambda: fh.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("apk")
    parser.add_argument("--json-out")
    args = parser.parse_args()

    apk = Path(args.apk)
    failures: list[str] = []
    report: dict[str, object] = {
        "apk": str(apk),
        "sha256": sha256(apk),
        "size_bytes": apk.stat().st_size,
    }

    with zipfile.ZipFile(apk) as zf:
        names = zf.namelist()
        dex_names = sorted(
            name for name in names
            if re.fullmatch(r"classes(?:\d+)?\.dex", name)
        )
        report["dex_files"] = dex_names
        if not dex_names:
            failures.append("APK contains no classes*.dex entries")

        app_locations = []
        for name in dex_names:
            if APPLICATION_DESCRIPTOR in zf.read(name):
                app_locations.append(name)
        report["application_class_locations"] = app_locations
        if not app_locations:
            failures.append("VkVideoApplication descriptor is missing from all DEX files")

        native_path = "lib/arm64-v8a/libvkcore.so"
        if native_path not in names:
            failures.append(f"missing required ARM64 native library: {native_path}")
            report["native"] = {"present": False}
        else:
            native = zf.read(native_path)
            old_count = count_pattern(native, ORIGINAL_NATIVE_PATTERN)
            new_count = count_pattern(native, PATCHED_NATIVE_PATTERN)
            report["native"] = {
                "present": True,
                "original_pattern_count": old_count,
                "patched_pattern_count": new_count,
            }
            if old_count != 0:
                failures.append(f"unpatched native signature pattern remains {old_count} time(s)")
            if new_count != 1:
                failures.append(f"patched native signature pattern count is {new_count}, expected 1")

        v1_entries = [name for name in names if V1_SIGNATURE_RE.match(name)]
        report["v1_signature_entries"] = v1_entries
        if v1_entries:
            failures.append("JAR/v1 signature entries are present: " + ", ".join(v1_entries))

    report["success"] = not failures
    report["failures"] = failures

    if args.json_out:
        Path(args.json_out).write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")

    if failures:
        for failure in failures:
            print(f"ERROR: {failure}", file=sys.stderr)
        return 1

    print(
        f"Static APK gate passed: {len(report['dex_files'])} DEX file(s), "
        f"application class in {report['application_class_locations']}, native bypass verified."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
