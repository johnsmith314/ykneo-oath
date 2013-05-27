package pkgYkneoOath;

/*
 * Copyright (c) 2013 Yubico AB
 * All rights reserved.
 */

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class OathObj {
	public static final byte HMAC_SHA1 = 0x01;
	public static final byte HMAC_SHA256 = 0x02;
	
	public static final byte PROP_ALWAYS_INCREASING = 1 << 0;
	
	private static short _0 = 0;
	
	public static OathObj firstObject;
	public static OathObj lastObject;
	public OathObj nextObject;
	
	private byte[] key;
	private byte[] name;
	public byte type;
	
	private byte[] lastChal;
	private byte props;
	
	public void setKey(byte[] buf, short offs, byte type, short len) {
		if(type != HMAC_SHA1 && type != HMAC_SHA256) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		this.type = type;
		if(key == null || key.length != len) {
			key = new byte[len];
		}
		Util.arrayCopy(buf, offs, key, _0, len);
	}
	
	public void setName(byte[] buf, short offs, short len) {
		name = new byte[len];
		Util.arrayCopy(buf, offs, name, _0, len);
	}
	
	public short getName(byte[] buf, short offs) {
		Util.arrayCopy(name, _0, buf, offs, (short) name.length);
		return (short) name.length;
	}
	
	public short getNameLength() {
		return (short) name.length;
	}
	
	public void setProp(byte props) {
		this.props = props;
	}
	
	public void addObject() {
		if(firstObject == null) {
			firstObject = lastObject = this;
		} else if(firstObject == lastObject) {
			firstObject.nextObject = lastObject = this;
		} else {
			lastObject.nextObject = lastObject = this;
		}
	}
	
	public void removeObject() {
		if(firstObject == lastObject && firstObject == this) {
			firstObject = lastObject = null;
		} else if(firstObject == this) {
			firstObject = this.nextObject;
		} else {
			OathObj object = firstObject;
			while(object.nextObject != this) {
				object = object.nextObject;
			}
			object.nextObject = nextObject;
			if(lastObject == this) {
				lastObject = object;
			}
		}
		this.nextObject = null;
	}
	
	public static OathObj findObject(byte[] name, short offs, short len) {
		OathObj object = firstObject;
		while(object != null) {
			if(len != object.name.length) {
				continue;
			}
			if(Util.arrayCompare(name, offs, object.name, _0, len) == 0) {
				break;
			}
			object = object.nextObject;
		}
		return object;
	}
}
