/** This class file was automatically generated by jASN1 (http://www.beanit.com) */
package com.beanit.josistack.internal.acse.asn1;

import com.beanit.jasn1.ber.BerLength;
import com.beanit.jasn1.ber.BerTag;
import com.beanit.jasn1.ber.ReverseByteArrayOutputStream;
import com.beanit.jasn1.ber.types.BerAny;
import com.beanit.jasn1.ber.types.BerBitString;
import com.beanit.jasn1.ber.types.BerInteger;
import com.beanit.jasn1.ber.types.BerObjectIdentifier;
import com.beanit.jasn1.ber.types.BerOctetString;
import com.beanit.jasn1.ber.types.BerType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Myexternal2 implements BerType, Serializable {

  public static final BerTag tag = new BerTag(BerTag.UNIVERSAL_CLASS, BerTag.CONSTRUCTED, 8);
  private static final long serialVersionUID = 1L;
  public byte[] code = null;
  private BerObjectIdentifier directReference = null;
  private BerInteger indirectReference = null;
  private Encoding encoding = null;

  public Myexternal2() {}

  public Myexternal2(byte[] code) {
    this.code = code;
  }

  public BerObjectIdentifier getDirectReference() {
    return directReference;
  }

  public void setDirectReference(BerObjectIdentifier directReference) {
    this.directReference = directReference;
  }

  public BerInteger getIndirectReference() {
    return indirectReference;
  }

  public void setIndirectReference(BerInteger indirectReference) {
    this.indirectReference = indirectReference;
  }

  public Encoding getEncoding() {
    return encoding;
  }

  public void setEncoding(Encoding encoding) {
    this.encoding = encoding;
  }

  public int encode(OutputStream reverseOS) throws IOException {
    return encode(reverseOS, true);
  }

  public int encode(OutputStream reverseOS, boolean withTag) throws IOException {

    if (code != null) {
      for (int i = code.length - 1; i >= 0; i--) {
        reverseOS.write(code[i]);
      }
      if (withTag) {
        return tag.encode(reverseOS) + code.length;
      }
      return code.length;
    }

    int codeLength = 0;
    codeLength += encoding.encode(reverseOS);

    if (indirectReference != null) {
      codeLength += indirectReference.encode(reverseOS, true);
    }

    if (directReference != null) {
      codeLength += directReference.encode(reverseOS, true);
    }

    codeLength += BerLength.encodeLength(reverseOS, codeLength);

    if (withTag) {
      codeLength += tag.encode(reverseOS);
    }

    return codeLength;
  }

  public int decode(InputStream is) throws IOException {
    return decode(is, true);
  }

  public int decode(InputStream is, boolean withTag) throws IOException {
    int codeLength = 0;
    int subCodeLength = 0;
    BerTag berTag = new BerTag();

    if (withTag) {
      codeLength += tag.decodeAndCheck(is);
    }

    BerLength length = new BerLength();
    codeLength += length.decode(is);

    int totalLength = length.val;
    codeLength += totalLength;

    subCodeLength += berTag.decode(is);
    if (berTag.equals(BerObjectIdentifier.tag)) {
      directReference = new BerObjectIdentifier();
      subCodeLength += directReference.decode(is, false);
      subCodeLength += berTag.decode(is);
    }

    if (berTag.equals(BerInteger.tag)) {
      indirectReference = new BerInteger();
      subCodeLength += indirectReference.decode(is, false);
      subCodeLength += berTag.decode(is);
    }

    encoding = new Encoding();
    subCodeLength += encoding.decode(is, berTag);
    if (subCodeLength == totalLength) {
      return codeLength;
    }
    throw new IOException(
        "Unexpected end of sequence, length tag: "
            + totalLength
            + ", actual sequence length: "
            + subCodeLength);
  }

  public void encodeAndSave(int encodingSizeGuess) throws IOException {
    ReverseByteArrayOutputStream reverseOS = new ReverseByteArrayOutputStream(encodingSizeGuess);
    encode(reverseOS, false);
    code = reverseOS.getArray();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    appendAsString(sb, 0);
    return sb.toString();
  }

  public void appendAsString(StringBuilder sb, int indentLevel) {

    sb.append("{");
    boolean firstSelectedElement = true;
    if (directReference != null) {
      sb.append("\n");
      for (int i = 0; i < indentLevel + 1; i++) {
        sb.append("\t");
      }
      sb.append("directReference: ").append(directReference);
      firstSelectedElement = false;
    }

    if (indirectReference != null) {
      if (!firstSelectedElement) {
        sb.append(",\n");
      }
      for (int i = 0; i < indentLevel + 1; i++) {
        sb.append("\t");
      }
      sb.append("indirectReference: ").append(indirectReference);
      firstSelectedElement = false;
    }

    if (!firstSelectedElement) {
      sb.append(",\n");
    }
    for (int i = 0; i < indentLevel + 1; i++) {
      sb.append("\t");
    }
    if (encoding != null) {
      sb.append("encoding: ");
      encoding.appendAsString(sb, indentLevel + 1);
    } else {
      sb.append("encoding: <empty-required-field>");
    }

    sb.append("\n");
    for (int i = 0; i < indentLevel; i++) {
      sb.append("\t");
    }
    sb.append("}");
  }

  public static class Encoding implements BerType, Serializable {

    private static final long serialVersionUID = 1L;

    public byte[] code = null;
    private BerAny singleASN1Type = null;
    private BerOctetString octetAligned = null;
    private BerBitString arbitrary = null;

    public Encoding() {}

    public Encoding(byte[] code) {
      this.code = code;
    }

    public BerAny getSingleASN1Type() {
      return singleASN1Type;
    }

    public void setSingleASN1Type(BerAny singleASN1Type) {
      this.singleASN1Type = singleASN1Type;
    }

    public BerOctetString getOctetAligned() {
      return octetAligned;
    }

    public void setOctetAligned(BerOctetString octetAligned) {
      this.octetAligned = octetAligned;
    }

    public BerBitString getArbitrary() {
      return arbitrary;
    }

    public void setArbitrary(BerBitString arbitrary) {
      this.arbitrary = arbitrary;
    }

    public int encode(OutputStream reverseOS) throws IOException {

      if (code != null) {
        for (int i = code.length - 1; i >= 0; i--) {
          reverseOS.write(code[i]);
        }
        return code.length;
      }

      int codeLength = 0;
      int sublength;

      if (arbitrary != null) {
        codeLength += arbitrary.encode(reverseOS, false);
        // write tag: CONTEXT_CLASS, PRIMITIVE, 2
        reverseOS.write(0x82);
        codeLength += 1;
        return codeLength;
      }

      if (octetAligned != null) {
        codeLength += octetAligned.encode(reverseOS, false);
        // write tag: CONTEXT_CLASS, PRIMITIVE, 1
        reverseOS.write(0x81);
        codeLength += 1;
        return codeLength;
      }

      if (singleASN1Type != null) {
        sublength = singleASN1Type.encode(reverseOS);
        codeLength += sublength;
        codeLength += BerLength.encodeLength(reverseOS, sublength);
        // write tag: CONTEXT_CLASS, CONSTRUCTED, 0
        reverseOS.write(0xA0);
        codeLength += 1;
        return codeLength;
      }

      throw new IOException("Error encoding CHOICE: No element of CHOICE was selected.");
    }

    public int decode(InputStream is) throws IOException {
      return decode(is, null);
    }

    public int decode(InputStream is, BerTag berTag) throws IOException {

      int codeLength = 0;
      BerTag passedTag = berTag;

      if (berTag == null) {
        berTag = new BerTag();
        codeLength += berTag.decode(is);
      }

      if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.CONSTRUCTED, 0)) {
        codeLength += BerLength.skip(is);
        singleASN1Type = new BerAny();
        codeLength += singleASN1Type.decode(is, null);
        return codeLength;
      }

      if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.PRIMITIVE, 1)) {
        octetAligned = new BerOctetString();
        codeLength += octetAligned.decode(is, false);
        return codeLength;
      }

      if (berTag.equals(BerTag.CONTEXT_CLASS, BerTag.PRIMITIVE, 2)) {
        arbitrary = new BerBitString();
        codeLength += arbitrary.decode(is, false);
        return codeLength;
      }

      if (passedTag != null) {
        return 0;
      }

      throw new IOException("Error decoding CHOICE: Tag " + berTag + " matched to no item.");
    }

    public void encodeAndSave(int encodingSizeGuess) throws IOException {
      ReverseByteArrayOutputStream reverseOS = new ReverseByteArrayOutputStream(encodingSizeGuess);
      encode(reverseOS);
      code = reverseOS.getArray();
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      appendAsString(sb, 0);
      return sb.toString();
    }

    public void appendAsString(StringBuilder sb, int indentLevel) {

      if (singleASN1Type != null) {
        sb.append("singleASN1Type: ").append(singleASN1Type);
        return;
      }

      if (octetAligned != null) {
        sb.append("octetAligned: ").append(octetAligned);
        return;
      }

      if (arbitrary != null) {
        sb.append("arbitrary: ").append(arbitrary);
        return;
      }

      sb.append("<none>");
    }
  }
}
