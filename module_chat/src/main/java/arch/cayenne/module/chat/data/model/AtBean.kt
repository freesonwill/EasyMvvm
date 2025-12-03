package arch.cayenne.module.chat.data.model

import arch.cayenne.module.chat.data.compare.AtBeanCompare

/**
 * @author: wenxi
 * @date: 24/11/25 11:26
 * @description:
 */
data class AtBean(val id:Int,val name:String,var isSelect:Boolean){
    fun builder(nId:Int = id,str:String = name,select:Boolean = isSelect):AtBean{
     return AtBean(nId,str,select)
    }

    override fun equals(other: Any?): Boolean {
        if(other == null ||other !is AtBean){
            return false
        }
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "$name,"
    }
}
