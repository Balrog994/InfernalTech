package com.balrog.InfernalTech.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PositionDirection {

	public BlockPos pos;
	public EnumFacing face;

	public PositionDirection(BlockPos pos, EnumFacing face) {
		this.pos = pos;
		this.face = face;

	}
	
	@Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((this.pos == null) ? 0 : this.pos.hashCode());
      result = prime * result + ((this.face == null) ? 0 : this.face.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj) {
        return true;
      }
      if(obj == null) {
        return false;
      }
      if(getClass() != obj.getClass()) {
        return false;
      }
      PositionDirection other = (PositionDirection) obj;
      if(this.pos == null) {
        if(other.pos != null) {
          return false;
        }
      } else if(!pos.equals(other.pos)) {
        return false;
      }
      if(this.face != other.face) {
        return false;
      }
      return true;
    }

}
