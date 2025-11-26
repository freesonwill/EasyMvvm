package arch.cayenne.module.chat.data.model

import arch.cayenne.module.chat.data.compare.AtBeanCompare

/**
 * @author: wenxi
 * @date: 24/11/25 11:26
 * @description:
 */
data class AtBean(val name:String,var isSelect:Boolean){
    fun builder(str:String = name,select:Boolean = isSelect):AtBean{
     return AtBean(str,select)
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
}
