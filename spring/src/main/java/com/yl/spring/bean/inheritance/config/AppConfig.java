package com.yl.spring.bean.inheritance.config;

import com.yl.spring.bean.inheritance.domain.Book;
import com.yl.spring.bean.inheritance.domain.EPubBook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public EPubBook ePubBook(){
        EPubBook ePubBook=new EPubBook();
        initBook(ePubBook);
        ePubBook.setDownloadUrl("http://example.eput.com/books/child");
        return ePubBook;
    }
    private void initBook(Book book){
        book.setBookName("child");
        book.setBookPrice(22.99f);
        book.setAuthorName("Sigid");
    }




}
