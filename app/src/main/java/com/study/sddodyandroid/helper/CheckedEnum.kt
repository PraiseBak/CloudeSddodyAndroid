package com.study.sddodyandroid.helper

class CheckedEnum (
        var infoEnum : InfoEnum,
        var isChecked : Boolean = false
){
    override fun toString(): String {
        return "$infoEnum"
    }
    companion object {
        fun convertCheckedEnum(infoEnumList: Array<out InfoEnum>): MutableList<CheckedEnum> {
            val resultList : MutableList<CheckedEnum> = mutableListOf()
            for(enum in infoEnumList){
                resultList.add(CheckedEnum(enum))
            }
            return resultList
        }
    }

}