package com.liao.framework.web.entity;

import com.liao.common.utils.Arith;
import com.liao.common.utils.ip.IpUtils;
import com.liao.framework.web.entity.server.*;
import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * 服务器相关信息
 * </p>
 *
 * @author LiAo
 * @since 2021/11/29
 */
@Data
public class Server {

    private static final int OSHI_WAIT_SECOND = 1000;

    // Cpu 信息
    private Cpu cpu = new Cpu();

    // 内存信息
    private Mem mem = new Mem();

    // Jvm 信息
    private Jvm jvm = new Jvm();

    // 系统先关信息
    private Sys sys = new Sys();

    // 磁盘相关信息
    private List<SysFile> sysFiles = new LinkedList<SysFile>();

    public void copyTo() {
        SystemInfo si = new SystemInfo();

        // 创建适应当前平台的实例
        HardwareAbstractionLayer hardware = si.getHardware();
        // 设置CPU信息
        setCpuInfo(hardware.getProcessor());
        // 设置内存信息c
        setMemInfo(hardware.getMemory());
        // 设置服务器信息
        setSysInfo();
        // 设置Java 虚拟机
        setJvmInfo();
        // 设置磁盘信息
        setSysFiles(si.getOperatingSystem());
    }

    /**
     * 设置CPU 信息
     */
    private void setCpuInfo(CentralProcessor processor) {
        // 获取系统范围的 CPU 负载滴答计数器
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        Util.sleep(OSHI_WAIT_SECOND);
        // 通过测量一个时间间隔内的滴答之间的差异，可以计算该间隔内的 CPU 负载。
        long[] ticks = processor.getSystemCpuLoadTicks();

        // CPU 使用率
        long nice = ticks[TickType.NICE.getIndex()] - prevTicks[TickType.NICE.getIndex()];
        long irq = ticks[TickType.IRQ.getIndex()] - prevTicks[TickType.IRQ.getIndex()];
        long softirq = ticks[TickType.SOFTIRQ.getIndex()] - prevTicks[TickType.SOFTIRQ.getIndex()];
        long steal = ticks[TickType.STEAL.getIndex()] - prevTicks[TickType.STEAL.getIndex()];
        long cSys = ticks[TickType.SYSTEM.getIndex()] - prevTicks[TickType.SYSTEM.getIndex()];
        long user = ticks[TickType.USER.getIndex()] - prevTicks[TickType.USER.getIndex()];
        long iowait = ticks[TickType.IOWAIT.getIndex()] - prevTicks[TickType.IOWAIT.getIndex()];
        long idle = ticks[TickType.IDLE.getIndex()] - prevTicks[TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        // CPU 核心数
        cpu.setCpuNum(processor.getLogicalProcessorCount());
        // CPU 使用率
        cpu.setTotal(totalCpu);
        // CPU 系统使用率
        cpu.setSys(cSys);
        // CPU 用户使用率
        cpu.setUsed(user);
        // CPU 当前等待率
        cpu.setWait(iowait);
        // CPU当前空闲率
        cpu.setFree(idle);
    }

    /**
     * 设置内存信息
     */
    private void setMemInfo(GlobalMemory memory) {
        // 实际物理内存量，以字节为单位。
        mem.setTotal(memory.getTotal());
        // 当前可用的物理内存量，以字节为单位。
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        // 当前可用的物理内存量，以字节为单位。
        mem.setFree(memory.getAvailable());
    }

    /**
     * 设置服务器信息
     */
    private void setSysInfo() {
        // 获取当前的运行属性
        Properties props = System.getProperties();
        // 服务器名称
        sys.setComputerName(IpUtils.getHostName());
        // 服务器IP
        sys.setComputerIp(IpUtils.getHostIp());
        // 服务器系统
        sys.setOsName(props.getProperty("os.name"));
        // 系统架构
        sys.setOsArch(props.getProperty("os.arch"));
        // 项目路径
        sys.setUserDir(props.getProperty("user.dir"));
    }

    /**
     * 设置Java 虚拟机
     */
    private void setJvmInfo() {
        // 获取当前的运行属性
        Properties props = System.getProperties();
        // JVM 占据内存数
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        // JVM 最大内存
        jvm.setMax(Runtime.getRuntime().maxMemory());
        // 虚拟机可用内存数
        jvm.setFree(Runtime.getRuntime().freeMemory());
        // 虚拟机版本
        jvm.setVersion(props.getProperty("java.version"));
        // 安装路径
        jvm.setHome(props.getProperty("java.home"));
    }

    /**
     * 设置磁盘信息
     */
    private void setSysFiles(OperatingSystem os) {
        // 实例化对象
        FileSystem fileSystem = os.getFileSystem();
        // 储实例化OSFileStore对象列表
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            // 驱动器可用磁盘空间
            long free = fs.getUsableSpace();
            // 驱动器的总空间/容量
            long total = fs.getTotalSpace();
            // 驱动器已用
            long used = total - free;
            SysFile sysFile = new SysFile();
            // 盘符路径
            sysFile.setDirName(fs.getMount());
            // 盘符类型
            sysFile.setSysTypeName(fs.getType());
            // 文件类型
            sysFile.setTypeName(fs.getName());
            // 总大小
            sysFile.setTotal(convertFileSize(total));
            // 剩余大小
            sysFile.setFree(convertFileSize(free));
            // 已经使用量
            sysFile.setUsed(convertFileSize(used));
            // 资源使用率
            sysFile.setUsage(Arith.mul(Arith.div(used, total, 4), 100));
            sysFiles.add(sysFile);
        }
    }

    /**
     * 字节转换
     *
     * @param size 字节大小
     * @return 转换后值
     */
    public String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format("%d B", size);
        }
    }
}
