package com.chaeniiz.kakaobooksearch.common.util

import java.text.SimpleDateFormat
import java.util.*

object FormatUtils {
    
    /**
     * 가격을 세자리 단위로 콤마를 찍어서 포매팅합니다.
     * @param price 가격 (Int)
     * @return 포매팅된 가격 문자열 (예: "10,000원")
     */
    fun formatPrice(price: Int): String {
        return "${String.format(Locale.getDefault(),"%,d", price)}원"
    }
    
    /**
     * 판매가를 포매팅합니다. 판매가가 유효하지 않은 경우 정상가를 사용합니다.
     * @param salePrice 판매가 (Int)
     * @param originalPrice 정상가 (Int)
     * @return 포매팅된 가격 문자열
     */
    fun formatSalePrice(salePrice: Int, originalPrice: Int): String {
        val validPrice = if (salePrice > 0) salePrice else originalPrice
        return formatPrice(validPrice)
    }
    
    /**
     * 날짜를 yyyy년 mm월 dd일 형식으로 포매팅합니다.
     * @param dateString 원본 날짜 문자열 (ISO 8601 형식: yyyy-MM-dd'T'HH:mm:ss.SSSXXX)
     * @return 포매팅된 날짜 문자열 (예: "2023년 12월 25일")
     */
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            try {
                val datePart = dateString.substring(0, 10)
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                
                val date = inputFormat.parse(datePart)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e2: Exception) {
                dateString
            }
        }
    }
}
