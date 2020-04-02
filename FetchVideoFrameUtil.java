package com.softsec.demo;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Created with IDEA
 * 视频取帧，可设置间隔秒数或获取所有帧数
 *
 * @Author Chensj
 * @Date 2018/4/2 14:29
 * @Description
 * @Version 1.0
 */
public class FetchVideoFrameUtil {

    // 主函数
    public static void main(String[] args){
        try {
            String picPath = "D:\\demo\\pic\\";  // 提取得每帧图片存放位置
            String videoPath = "D:\\demo1.mp4";  // 原视频文件路径
            int second = 0; // 每隔多少帧取一张图，一般高清视频每秒 20-24 帧，根据情况配置，如果全部提取，则将second设为 0 即可
            // 开始视频取帧流程
            FetchVideoFrameUtil.fetchPic(new File(videoPath),picPath,second);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定视频的帧并保存为图片至指定目录
     * @param file  源视频文件
     * @param picPath  截取帧的图片存放路径
     * @throws Exception
     */
    public static void fetchPic(File file, String picPath,int second) throws Exception{

        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file); // 获取视频文件

        System.out.println(FetchVideoFrameUtil.getVideoTime(file)); // 显示视频长度（秒/s）

        ff.start(); // 调用视频文件播放
        int length = ff.getLengthInAudioFrames(); //视频帧数长度
        System.out.println(ff.getFrameRate());

        int i = 0; // 图片帧数，如需跳过前几秒，则在下方过滤即可
        Frame frame = null;
        int count = 0;
        while (i < length) {
            frame = ff.grabImage(); // 获取该帧图片流
            System.out.print(i + ",");
            if(frame!=null && frame.image!=null) {
                System.out.println(i);
                writeToFile(frame, picPath, count,second); // 生成帧图片
                count++;
            }
            i++;
        }
        ff.stop();
    }

    /**
     *
     * @param frame // 视频文件对象
     * @param picPath // 图片存放路径
     * @param count // 当前取到第几帧
     * @param second // 每隔多少帧取一张，一般高清视频每秒 20-24 帧，根据情况配置，如果全部提取，则将second设为 0 即可
     */
    public static void writeToFile(Frame frame, String picPath, int count, int second){
        if (second == 0) {
            // 跳过间隔取帧判断
        } else if (count % second != 0){ // 提取倍数，如每秒取一张，则： second = 20
           return;
        }
        File targetFile = new File(picPath + count + ".jpg");
        System.out.println("创建了文件：" + picPath + count + ".jpg");
        String imgSuffix = "jpg";

        Java2DFrameConverter converter =new Java2DFrameConverter();
        BufferedImage srcBi =converter.getBufferedImage(frame);
        int owidth = srcBi.getWidth();
        int oheight = srcBi.getHeight();
        // 对截取的帧进行等比例缩放
        int width = 800;
        int height = (int) (((double) width / owidth) * oheight);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        bi.getGraphics().drawImage(srcBi.getScaledInstance(width, height, Image.SCALE_SMOOTH),0, 0, null);
        try {
            ImageIO.write(bi, imgSuffix, targetFile);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取视频时长，单位为秒
     * @param file
     * @return 时长（s）
     */
    public static Long getVideoTime(File file){
        Long times = 0L;
        try {
            FFmpegFrameGrabber ff = new FFmpegFrameGrabber(file);
            ff.start();
            times = ff.getLengthInTime()/(1000*1000);
            ff.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }
}

