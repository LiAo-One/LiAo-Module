package com.liao.system.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liao.common.core.R;
import com.liao.common.core.page.PageUtils;
import com.liao.common.exception.BusinessException;
import com.liao.common.exception.ServiceException;
import com.liao.common.exception.check.MissingParametersException;
import com.liao.common.exception.user.LoginExpiredException;
import com.liao.common.utils.RedisUtil;
import com.liao.common.utils.StringUtils;
import com.liao.common.utils.TokenUtil;
import com.liao.common.utils.poi.ExcelUtil;
import com.liao.system.dao.SysAdminMapper;
import com.liao.system.dao.SysAdminRoleMapper;
import com.liao.system.dao.SysRoleMapper;
import com.liao.common.core.entity.SysAdmin;
import com.liao.system.entity.SysAdminRole;
import com.liao.system.entity.SysRole;
import com.liao.system.services.SysAdminRoleService;
import com.liao.system.services.SysAdminService;
import com.liao.system.services.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员 服务实现类
 * </p>
 *
 * @author LiAo
 * @since 2020-12-14
 */
@Service
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements SysAdminService {

    // 管理员
    @Autowired
    private SysAdminMapper sysAdminMapper;

    // Redis 操作
    @Autowired
    private RedisUtil redisUtil;

    // 角色与菜单
    @Autowired
    private SysAdminRoleMapper sysAdminRoleMapper;

    // 角色
    @Autowired
    private SysRoleMapper sysRoleMapper;

    // 按钮
    @Autowired
    private SysMenuService sysMenuService;

    // 用户与角色
    @Autowired
    private SysAdminRoleService sysAdminRoleService;


    /**
     * 获取当前登录用户数据
     *
     * @return 用户信息
     */
    @Override
    public SysAdmin getLoginInfo() {
        // 获取登录用户Token
        String token = TokenUtil.getLoginUserToken();

        SysAdmin loginInfo = (SysAdmin) redisUtil.get(token);

        // 为空抛异常
        if (StringUtils.isNull(loginInfo)) {
            throw new LoginExpiredException();
        }
        return loginInfo;
    }


    /**
     * 退出登录
     *
     * @param token token
     * @return 结果
     */
    @Override
    public R logout(String token) {
        redisUtil.del(token,
                TokenUtil.getMenuTokenKey(token),
                TokenUtil.getUserTokenKey(token),
                TokenUtil.getRoleTokenKey(token));
        return R.success();
    }


    /**
     * 分页查询
     *
     * @param recode 条件
     * @return 结果
     */
    @Override
    public R selPage(SysAdmin recode) {
        // 分页信息
        IPage<SysAdmin> page = PageUtils.startPage();

        // 排序信息
        QueryWrapper<SysAdmin> wrapper = PageUtils.startOrderBy(getSysAdminWrapper(recode));

        // 返回结果
        return R.r(sysAdminMapper.selectPage(page, wrapper));
    }

    /**
     * 查询用户数据
     *
     * @param sysAdmin 条件
     * @return 结果
     */
    @Override
    public List<SysAdmin> selectUserList(SysAdmin sysAdmin) {

        return sysAdminMapper.selectList(getSysAdminWrapper(sysAdmin));
    }

    /**
     * 根据id查询数据
     *
     * @param id id
     * @return 结果
     */
    @Override
    public R findById(Long id) {

        SysAdmin sysAdmin = sysAdminMapper.selectById(id);

        if (StringUtils.isNull(sysAdmin)) {
            throw new BusinessException("用户查询为空");
        }

        Map<String, Object> map = new HashMap<>();

        // userRole
        SysRole sysRole = sysRoleMapper.selLoginUserRole(sysAdmin.getAdminId());

        // userRole
        map.put("user", sysAdmin);
        map.put("role", sysRole);
        map.put("roles", sysRoleMapper.selectList(null));

        return R.success(map);
    }

    /**
     * 根据ids查询数据
     *
     * @param ids ids
     * @return 结果
     */
    @Override
    public R findByIds(List<Long> ids) {
        return R.r(sysAdminMapper.selectBatchIds(ids));
    }

    /**
     * 根据名称查询有没有这个用户
     *
     * @param name 名称
     * @return 结果
     */
    @Override
    public List<SysAdmin> selectUserByUserName(String name) {
        return sysAdminMapper.selectList(new QueryWrapper<SysAdmin>()
                .eq("admin_account", name));
    }

    /**
     * 添加数据
     *
     * @param recode 添加参数
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R add(SysAdmin recode, Long roleId) {

        sysAdminMapper.insert(recode);

        // 角色id不为空
        if (StringUtils.isNotEmpty(roleId)) {
            SysAdminRole sysAdminRole = new SysAdminRole();

            sysAdminRole.setAdminId(recode.getAdminId());
            sysAdminRole.setRoleId(roleId);

            sysAdminRoleService.add(sysAdminRole);
        }

        return R.success();
    }

    /**
     * 根据id修改
     *
     * @param recode 修改参数
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public R updById(SysAdmin recode, Long roleId) {
        if (StringUtils.isEmpty(recode.getAdminId())) {
            throw new MissingParametersException("管理员ID");
        }
        // 角色信息更新
        if (StringUtils.isNotNull(roleId)) {
            SysAdminRole sysAdminRole = new SysAdminRole();
            sysAdminRole.setAdminId(recode.getAdminId());
            sysAdminRole.setRoleId(roleId);
            // 清除缓存
            sysAdminRoleMapper.deleteById(roleId);
            // 执行添加
            sysAdminRoleMapper.insert(sysAdminRole);
        }
        // 用户信息更新
        sysAdminMapper.updateById(recode);

        return R.success();
    }

    /**
     * 根据id删除
     *
     * @param id id
     * @return 结果
     */
    @Override
    public R delete(Long id) {
        if (StringUtils.isEmpty(id)) {
            throw new MissingParametersException("管理员ID");
        }
        return R.r(sysAdminMapper.deleteById(id));
    }

    /**
     * 根据id批量删除
     *
     * @param ids id集合
     * @return 结果
     */
    @Override
    public R deletes(List<Long> ids) {
        return R.r(sysAdminMapper.deleteBatchIds(ids));
    }

    /**
     * 导入数据
     *
     * @param file 文件
     * @return 结果
     */
    @Override
    public String importData(MultipartFile file, boolean isUpdate) {
        ExcelUtil<SysAdmin> util = new ExcelUtil<>(SysAdmin.class);
        List<SysAdmin> userList = null;

        try {
            userList = util.importExcel(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new ServiceException("导入用户数据不能为空！");
        }

        // 成功数量
        int successNum = 0;
        // 失败数量
        int failureNum = 0;

        // 成功消息
        StringBuilder successMsg = new StringBuilder();
        // 失败消息
        StringBuilder failureMsg = new StringBuilder();

        for (SysAdmin sysAdmin : userList) {

            try {
                List<SysAdmin> sysAdmins = selectUserByUserName(sysAdmin.getAdminAccount());

                // 数据不存在就执行更新
                if (StringUtils.isEmpty(sysAdmins)) {
                    sysAdminMapper.insert(sysAdmin);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ")
                            .append(sysAdmin.getAdminAccount()).append(" 导入成功");
                }
                // 数据存在执行更新
                else if (isUpdate) {
                    // 获取主键
                    sysAdmin.setAdminId(sysAdmins.get(0).getAdminId());
                    // 根据主键更新
                    sysAdminMapper.updateById(sysAdmin);
                    successNum++;
                    successMsg.append("<br/>").append(successNum).append("、账号 ")
                            .append(sysAdmin.getAdminAccount()).append(" 导入成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ")
                            .append(sysAdmin.getAdminAccount()).append(" 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + sysAdmin.getAdminAccount() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
            }
        }

        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        return successMsg.toString();
    }

    public QueryWrapper<SysAdmin> getSysAdminWrapper(SysAdmin recode) {

        // 构造条件
        QueryWrapper<SysAdmin> queryWrapper = new QueryWrapper<>();

        // 管理员主键SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminId()), "admin_id", recode.getAdminId());
        // 账户SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminAccount()), "admin_account", recode.getAdminAccount());
        // 密码SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminPassword()), "admin_password", recode.getAdminPassword());
        // 姓名SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminName()), "admin_name", recode.getAdminName());
        // 性别SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminSex()), "admin_sex", recode.getAdminSex());
        // 头像连接SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminAvatar()), "admin_avatar", recode.getAdminAvatar());
        // 邮箱SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminEmail()), "admin_email", recode.getAdminEmail());
        // 昵称SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminNickname()), "admin_nickname", recode.getAdminNickname());
        // 备注SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getAdminRemarks()), "admin_remarks", recode.getAdminRemarks());
        // 创建时间SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getCreateTime()), "create_time", recode.getCreateTime());
        // 修改时间SysOpenLogServiceImpl.java
        queryWrapper.eq(StringUtils.isNotNull(recode.getUpdateTime()), "update_time", recode.getUpdateTime());

        return queryWrapper;
    }
}
