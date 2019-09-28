package lsafer.util.impl;

import lsafer.io.File;
import lsafer.io.SerialFileMap;
import lsafer.util.HybridMap;
import lsafer.util.JetMap;

/**
 * An implement of 3 interfaces. To be a map that have a 3rd Serial-file container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (28-Sep-19)
 * @see java.io.Serializable
 * @since 25-Sep-19
 */
@SuppressWarnings("unused")
public class SerialFileHashMap<K, V> extends IOHashMap<File, K, V> implements SerialFileMap<K, V>, JetMap<K, V>, HybridMap<K, V> {
}
