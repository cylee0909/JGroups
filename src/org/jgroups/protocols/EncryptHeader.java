package org.jgroups.protocols;

import org.jgroups.Global;
import org.jgroups.Header;
import org.jgroups.util.Util;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.function.Supplier;

/**
 * @author Bela Ban
 * @since  4.0
 */
public class EncryptHeader extends Header {
    public static final byte ENCRYPT        = 1 << 0;
    public static final byte SECRET_KEY_REQ = 1 << 1;
    public static final byte SECRET_KEY_RSP = 1 << 2;

    protected byte   type;
    protected byte[] version;
    protected byte[] signature; // the encrypted checksum


    public EncryptHeader() {}


    public EncryptHeader(byte type, byte[] version) {
        this.type=type;
        this.version=version;
    }

    public byte          type()              {return type;}
    public byte[]        version()           {return version;}
    public byte[]        signature()         {return signature;}
    public EncryptHeader signature(byte[] s) {this.signature=s; return this;}
    public short getMagicId() {return 88;}
    public Supplier<? extends Header> create() {
        return EncryptHeader::new;
    }

    public void writeTo(DataOutput out) throws Exception {
        out.writeByte(type);
        Util.writeByteBuffer(version, 0, version != null? version.length : 0, out);
        Util.writeByteBuffer(signature, 0, signature != null? signature.length : 0, out);
    }

    public void readFrom(DataInput in) throws Exception {
        type=in.readByte();
        version=Util.readByteBuffer(in);
        signature=Util.readByteBuffer(in);
    }

    public String toString() {
        return String.format("[%s version=%s]", typeToString(type), (version != null? version.length + " bytes" : "n/a"));
    }

    public int serializedSize() {return Global.BYTE_SIZE + Util.size(version) + Util.size(signature) /*+ Util.size(payload) */;}

    protected static String typeToString(byte type) {
        switch(type) {
            case ENCRYPT:        return "ENCRYPT";
            case SECRET_KEY_REQ: return "SECRET_KEY_REQ";
            case SECRET_KEY_RSP: return "SECRET_KEY_RSP";
            default:             return "<unrecognized type " + type;
        }
    }
}
