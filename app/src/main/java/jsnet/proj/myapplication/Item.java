package jsnet.proj.myapplication;

public class Item {
    String image;
    String title;
    String regdate;
    String content;
    String sno;
    String skin_num;
    String pro_img;
    String able_java;
    String news_code;

    String getImage() {
        return this.image;
    }
    String getTitle() {
        return this.title;
    }
    String getsno() {
        return this.sno;
    }
    String getcontent() {
        return this.content;
    }
    String getregdate() {
        return this.regdate;
    }
    String getskin_num() {
        return this.skin_num;
    }
    String getable_java() {
        return this.able_java;
    }
    String getnews_code() {
        return this.news_code;
    }


    String getpro_img() {
        return this.pro_img;
    }

    Item(String image, String title, String sno, String content, String regdate, String skin_num, String pro_img, String able_java, String news_code) {
        this.image = image;
        this.title = title;
        this.sno = sno;
        this.content = content;
        this.regdate = regdate;
        this.skin_num = skin_num;
        this.pro_img = pro_img;
        this.able_java = able_java;
        this.news_code = news_code;
    }
}
