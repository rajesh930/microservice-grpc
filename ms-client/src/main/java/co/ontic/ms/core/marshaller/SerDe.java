package co.ontic.ms.core.marshaller;

/**
 * @author rajesh
 * @since 15/01/25 20:02
 */
public interface SerDe {
    /**
     * Serialize obj to bytes array
     */
    byte[] serialize(Object obj);

    /**
     * Deserialize bytes array back to Object
     */
    Object deSerialize(byte[] bytes);
}
