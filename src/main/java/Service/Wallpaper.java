package Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import Domian.Images;
import Utils.FileUtils;
import Utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author niujinpeng
 * @date 2021/02/08
 * @link https://github.com/niumoo
 */
public class Wallpaper {

    // BING API
    private static String BING_API = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&nc=1620441890644&pid=hp";

    private static String BING_URL = "https://cn.bing.com";

    public static void main(String[] args) throws IOException {
        String httpContent = HttpUtils.getHttpContent(BING_API);
        JSONObject jsonObject = JSON.parseObject(httpContent);
        JSONArray jsonArray = jsonObject.getJSONArray("images");

        jsonObject = (JSONObject)jsonArray.get(0);
        // 图片地址
        String url = BING_URL + (String)jsonObject.get("url");
        //这个加不加关系不大
        url = url.substring(0, url.indexOf("&"));

        // 图片时间
        String enddate = (String)jsonObject.get("enddate");
        LocalDate localDate = LocalDate.parse(enddate, DateTimeFormatter.BASIC_ISO_DATE);
        enddate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        // 图片版权
        String copyright = (String)jsonObject.get("copyright");
        //读取本地已存在图片信息将之放入内存之中
        List<Images> imagesList = FileUtils.readBing();
        //将今天最新图片信息更新到第一行
        imagesList.set(0,new Images(copyright, enddate, url));
        //去除 重复出现的图片信息
        imagesList = imagesList.stream().distinct().collect(Collectors.toList());
        //将内存中的图片信息重新写入文本中
        FileUtils.writeBing(imagesList);
        FileUtils.writeReadme(imagesList);

    }

}
