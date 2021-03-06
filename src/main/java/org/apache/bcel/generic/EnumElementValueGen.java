/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.ElementValue;
import org.apache.bcel.classfile.EnumElementValue;

/**
 * @since 6.0
 */
public class EnumElementValueGen extends ElementValueGen
{
    // For enum types, these two indices point to the type and value
    private int typeIdx;

    private int valueIdx;

    /**
     * This ctor assumes the constant pool already contains the right type and
     * value - as indicated by typeIdx and valueIdx. This ctor is used for
     * deserialization
     */
    protected EnumElementValueGen(int typeIdx, int valueIdx,
            ConstantPoolGen cpool)
    {
        super(ElementValueGen.ENUM_CONSTANT, cpool);
        if (type != ENUM_CONSTANT) {
            throw new RuntimeException(
                    "Only element values of type enum can be built with this ctor - type specified: " + type);
        }
        this.typeIdx = typeIdx;
        this.valueIdx = valueIdx;
    }

    /**
     * Return immutable variant of this EnumElementValue
     */
    @Override
    public ElementValue getElementValue()
    {
        System.err.println("Duplicating value: " + getEnumTypeString() + ":"
                + getEnumValueString());
        return new EnumElementValue(type, typeIdx, valueIdx, cpGen
                .getConstantPool());
    }

    public EnumElementValueGen(ObjectType t, String value, ConstantPoolGen cpool)
    {
        super(ElementValueGen.ENUM_CONSTANT, cpool);
        typeIdx = cpool.addUtf8(t.getSignature());// was addClass(t);
        valueIdx = cpool.addUtf8(value);// was addString(value);
    }

    public EnumElementValueGen(EnumElementValue value, ConstantPoolGen cpool,
            boolean copyPoolEntries)
    {
        super(ENUM_CONSTANT, cpool);
        if (copyPoolEntries)
        {
            typeIdx = cpool.addUtf8(value.getEnumTypeString());// was
                                                                // addClass(value.getEnumTypeString());
            valueIdx = cpool.addUtf8(value.getEnumValueString()); // was
                                                                    // addString(value.getEnumValueString());
        }
        else
        {
            typeIdx = value.getTypeIndex();
            valueIdx = value.getValueIndex();
        }
    }

    @Override
    public void dump(DataOutputStream dos) throws IOException
    {
        dos.writeByte(type); // u1 type of value (ENUM_CONSTANT == 'e')
        dos.writeShort(typeIdx); // u2
        dos.writeShort(valueIdx); // u2
    }

    @Override
    public String stringifyValue()
    {
        ConstantUtf8 cu8 = (ConstantUtf8) getConstantPool().getConstant(
                valueIdx);
        return cu8.getBytes();
        // ConstantString cu8 =
        // (ConstantString)getConstantPool().getConstant(valueIdx);
        // return
        // ((ConstantUtf8)getConstantPool().getConstant(cu8.getStringIndex())).getBytes();
    }

    // BCELBUG: Should we need to call utility.signatureToString() on the output
    // here?
    public String getEnumTypeString()
    {
        // Constant cc = getConstantPool().getConstant(typeIdx);
        // ConstantClass cu8 =
        // (ConstantClass)getConstantPool().getConstant(typeIdx);
        // return
        // ((ConstantUtf8)getConstantPool().getConstant(cu8.getNameIndex())).getBytes();
        return ((ConstantUtf8) getConstantPool().getConstant(typeIdx))
                .getBytes();
        // return Utility.signatureToString(cu8.getBytes());
    }

    public String getEnumValueString()
    {
        return ((ConstantUtf8) getConstantPool().getConstant(valueIdx))
                .getBytes();
        // ConstantString cu8 =
        // (ConstantString)getConstantPool().getConstant(valueIdx);
        // return
        // ((ConstantUtf8)getConstantPool().getConstant(cu8.getStringIndex())).getBytes();
    }

    public int getValueIndex()
    {
        return valueIdx;
    }

    public int getTypeIndex()
    {
        return typeIdx;
    }
}
