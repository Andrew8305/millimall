package com.millinch.mall.goods.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 商品属性
 * </p>
 *
 * @author John Zhang
 * @since 2017-02-16
 */
@TableName("milli_goods_attribute")
public class GoodsAttribute implements Serializable {

	private static final long serialVersionUID = -1104001463453023178L;

	@TableId(type = IdType.AUTO)
	private Long id;

	@TableField(value="goods_id")
	private Long goodsId;

	private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
