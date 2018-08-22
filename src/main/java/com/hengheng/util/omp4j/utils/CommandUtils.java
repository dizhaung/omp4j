package com.hengheng.util.omp4j.utils;

import cn.hutool.core.util.StrUtil;
import com.hengheng.util.omp4j.exceptions.OmpUtilsException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author panhb
 */
@Slf4j
public class CommandUtils {

    /**
     *
     * @param commandPath
     * @param arguments
     * @return String
     */
    @SneakyThrows
    public static String execute(String commandPath, String[] arguments) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        CommandLine commandline = new CommandLine(commandPath);
        commandline.addArguments(arguments, false);
        log.debug(commandline.toString());
        DefaultExecutor exec = new DefaultExecutor();
        int[] values = new int[] { 0, 1 };
        exec.setExitValues(values);
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        exec.setStreamHandler(streamHandler);
        int execute;
        try {
            execute = exec.execute(commandline);
        } catch (IOException e) {
            throw new OmpUtilsException("执行命令异常,cmd:"+commandline.toString()+","+e.getMessage());
        }
        String out,error;
        try {
            out = outputStream.toString("utf-8");
            error = errorStream.toString("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new OmpUtilsException("命令执行获取返回值错误,"+e.getMessage());
        }
        if(StrUtil.isNotBlank(error)){
            throw new OmpUtilsException(error);
        }
        if (execute == 0) {
            String[] infos = out.split("\n");
            StringBuilder sb = new StringBuilder();
            for(String info : infos){
                sb.append(info.trim());
            }
            return sb.toString();
        } else {
            throw new OmpUtilsException("执行命令错误,cmd:"+commandline.toString());
        }
    }
}
