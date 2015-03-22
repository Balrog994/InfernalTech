package com.balrog.InfernalTech.properties;

import java.util.Collection;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

import com.balrog.InfernalTech.enums.EnumFaceMode;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class PropertyFaceMode  extends PropertyEnum
{
	protected PropertyFaceMode(String name, Collection values)
    {
        super(name, EnumFaceMode.class, values);
    }

    public static PropertyFaceMode create(String name)
    {
        return create(name, Predicates.alwaysTrue());
    }

    public static PropertyFaceMode create(String name, Predicate filter)
    {
        return create(name, Collections2.filter(Lists.newArrayList(EnumFaceMode.values()), filter));
    }

    public static PropertyFaceMode create(String name, Collection values)
    {
        return new PropertyFaceMode(name, values);
    }
}
