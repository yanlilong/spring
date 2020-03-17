package com.yl.spring;

import com.yl.spring.bean.inheritance.config.AppConfig;
import com.yl.spring.bean.inheritance.domain.EPubBook;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx=new AnnotationConfigApplicationContext();
		ctx.register(AppConfig.class);
		ctx.refresh();
		EPubBook ePubBook=ctx.getBean(EPubBook.class);
		System.out.println("author name"+ePubBook.getAuthorName());
		System.out.println("Book Name: " + ePubBook.getBookName());
		System.out.println("Book Price: " + ePubBook.getBookPrice());
		System.out.println("Download URL: " + ePubBook.getDownloadUrl());
		SpringApplication.run(Application.class, args);
		ctx.registerShutdownHook();

	}

}
