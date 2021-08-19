package com.liao;

import com.liao.common.utils.StringUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2021/7/8
 */
public class Hello {

    public static void main(String[] args) {
        System.out.println(StringUtils.split(StringUtils.substringBetween("double", "(", ")"), ","));
    }
}
