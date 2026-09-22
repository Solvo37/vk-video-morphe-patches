import com.android.tools.smali.dexlib2.DexFileFactory;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile;
import com.android.tools.smali.dexlib2.iface.ClassDef;
import com.android.tools.smali.dexlib2.iface.MultiDexContainer;
import com.android.tools.smali.dexlib2.writer.io.FileDataStore;
import com.android.tools.smali.dexlib2.writer.pool.DexPool;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DexRepack {
    private static final int[] EXPECTED_COUNTS = {
        0,
        4, 9649, 13304, 11920, 12560, 11979, 14128,
        9351, 10243, 11125, 12842, 11926, 13474
    };

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException(
                "Usage: DexRepack <input.apk> <target-nibbles.bin> <output-dir>"
            );
        }

        File apk = new File(args[0]);
        byte[] targets = Files.readAllBytes(Path.of(args[1]));
        File outDir = new File(args[2]);
        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new IllegalStateException("Could not create " + outDir);
        }

        Opcodes opcodes = Opcodes.getDefault();
        MultiDexContainer<? extends DexBackedDexFile> container =
            DexFileFactory.loadDexContainer(apk, opcodes);

        Map<String, ClassDef> classes = new HashMap<>(180_000);
        for (String entryName : container.getDexEntryNames()) {
            DexBackedDexFile dex = container.getEntry(entryName).getDexFile();
            for (ClassDef cls : dex.getClasses()) {
                ClassDef previous = classes.put(cls.getType(), cls);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate class " + cls.getType());
                }
            }
        }

        List<String> descriptors = new ArrayList<>(classes.keySet());
        Collections.sort(descriptors);

        int capacity = targets.length * 2;
        if (descriptors.size() > capacity || descriptors.size() < capacity - 1) {
            throw new IllegalStateException(
                "Target map size mismatch: classes=" + descriptors.size() +
                " nibbleCapacity=" + capacity
            );
        }

        List<List<ClassDef>> groups = new ArrayList<>(14);
        groups.add(new ArrayList<>());
        for (int i = 1; i <= 13; i++) groups.add(new ArrayList<>());

        for (int i = 0; i < descriptors.size(); i++) {
            int packed = targets[i / 2] & 0xff;
            int target = (i % 2 == 0) ? (packed >>> 4) : (packed & 0x0f);
            if (target < 1 || target > 13) {
                throw new IllegalStateException(
                    "Invalid target " + target + " for " + descriptors.get(i)
                );
            }
            groups.get(target).add(classes.get(descriptors.get(i)));
        }

        int total = 0;
        for (int target = 1; target <= 13; target++) {
            List<ClassDef> group = groups.get(target);
            if (group.size() != EXPECTED_COUNTS[target]) {
                throw new IllegalStateException(
                    "classes" + (target == 1 ? "" : target) + ".dex count mismatch: " +
                    group.size() + " expected " + EXPECTED_COUNTS[target]
                );
            }

            String name = target == 1 ? "classes.dex" : "classes" + target + ".dex";
            File output = new File(outDir, name);
            DexPool pool = new DexPool(opcodes);
            for (ClassDef cls : group) pool.internClass(cls);
            pool.writeTo(new FileDataStore(output));
            System.out.println(name + ": " + group.size() + " classes, " + output.length() + " bytes");
            total += group.size();
        }

        if (total != descriptors.size()) {
            throw new IllegalStateException("Class total mismatch: " + total + " vs " + descriptors.size());
        }

        System.out.println("Repacked " + total + " classes into 13 DEX files.");
    }
}
