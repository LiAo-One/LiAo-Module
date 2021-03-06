package com.liao.system.services;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liao.common.core.R;
import com.liao.common.core.entity.SysAdmin;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 管理员 服务类
 * </p>
 *
 * @author LiAo
 * @since 2020-12-14
 */
public interface SysAdminService extends IService<SysAdmin> {

    /**
     * 获取当前登录用户数据
     *
     * @return 用户信息
     */
    SysAdmin getLoginInfo();

    /**
     * 退出登录
     *
     * @param token token
     * @return 结果
     */
    R logout(String token);

    /**
     * 分页查询 排序
     *
     * @param recode 条件
     * @return 结果
     */
    R selPage(SysAdmin recode);


    /**
     * 查询用户数据
     *
     * @param sysAdmin 条件
     * @return 结果
     */
    List<SysAdmin> selectUserList(SysAdmin sysAdmin);

    /**
     * 根据id查询数据
     *
     * @param id id
     * @return 结果
     */
    R findById(Long id);

    /**
     * 根据ids查询数据
     *
     * @param ids ids
     * @return 结果
     */
    R findByIds(List<Long> ids);

    /**
     * 根据名称查询有没有这个用户
     *
     * @param name 名称
     * @return 结果
     */
    List<SysAdmin> selectUserByUserName(String name);

    /**
     * 添加数据
     *
     * @param recode 添加参数
     * @return 结果
     */
    R add(SysAdmin recode, Long roleId);

    /**
     * 根据id修改用户
     *
     * @param recode 修改参数
     * @param roleId 角色id
     * @return 修改结果
     */
    R updById(SysAdmin recode, Long roleId);

    /**
     * 根据id删除
     *
     * @param id id
     * @return 结果
     */
    R delete(Long id);

    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 结果
     */
    R deletes(List<Long> ids);

    /**
     * 导入数据
     *
     * @param file 文件
     * @return 结果
     */
    String importData(MultipartFile file, boolean isUpdate);
}
