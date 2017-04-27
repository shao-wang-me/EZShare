# Test

要按照顺序测~

	sunrise.cis.unimelb.edu.au:3780
	sunrise.cis.unimelb.edu.au:3781

	java -jar EZShare.jar -debug -serect 123

## PUBLISH

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "baidu" -uri http://www.baidu.com -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri http://daringfireball.net/projects/markdown/syntax -channel markdown -owner DF -description "Daring Fireball: Markdown Syntax Documentation" -debug
  
overwrite

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -description "bilibili" -debug

### error: cannot publish resource

resource is a file

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri file://users/abc.java -debug

same channel, same URI, different owner

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -uri http://www.bilibili.com -channel carton -description "BILIBILI" -owner bilibili -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri http://daringfireball.net/projects/markdown/syntax -channel markdown -owner github -description "Daring Fireball: Markdown Syntax Documentation" -debug

URI missing

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -debug

### error: invalid resource

URI is empty (直接发JSON)

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -name "bilibili" -uri -debug

owner is "*" (直接发JSON)

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri http://www.dubai.com -owner * -debug

URI not official

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri www.melb.com -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri abc -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri 分布式系统 -debug

URI not absolute

	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri //www.internet.com/ -debug
	java -jar client.jar -host 10.13.126.138 -port 20006 -publish -uri about.html -debug

### error: missing resource (直接发JSON)

## SHARE

serect设置为123

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success

找一个电脑上确实有的文件！

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://C:/Users/shaow1/Desktop/1.jpeg

overwrite

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://C:/Users/shaow1/Desktop/1.jpeg -name "file"

### error: cannot share resource

not a file

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri http://www.baidu.com

same channel, same URI, different owner

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://C:/Users/shaow1/Desktop/1.jpeg -owner shaowang

not pointing to a file on the local file system

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://users/aaaaaaaaaaaa.txt
	(Pointing to a directory: Windows only)
	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://C:/

cannot be read as a file
找一个没有权限打开的文件，如：其他用户的文件 / Lab电脑上面的Users/config文件夹

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri file://C:/Users/Test/abc.java

### error: invalid resource

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123 -uri C:/Users/shaow1/Desktop/1.jpeg

### error: incorrect secret

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -uri file://C:/Users/shaow1/Desktop/1.jpeg -secret 12313123

### error: missing resource and\/or secret (直接发JSON)

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -uri file://C:/Users/shaow1/Desktop/1.jpeg
	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -share -secret 123

## REMOVE

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -remove -uri http://daringfireball.net/projects/markdown/syntax

### error: cannot remove resource

不存在该resource

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -remove -uri file://C:/Users/shaow1/Desktop/2.jpeg

Owner不对

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -remove -uri file://C:/Users/shaow1/Desktop/1.jpeg -owner shaowang

### error: invalid resource

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -remove -uri C:/Users/shaow1/Desktop/1.jpeg

### error: missing resource (直接发JSON)

## QUERY

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success [RESOURCE] resultSize:*
### error: invalid resourceTemplate
### error: missing resourceTemplate

## FETCH

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success [RESOURCE] resultSize:1

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -fetch -uri file://C:/Users/shaow1/Desktop/1.jpeg

### success resultSize:0

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -fetch -uri file://C:/Users/shaow1/Desktop/2.jpeg

### error: invalid resourceTemplate

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -fetch -uri http://C:/Users/shaow1/Desktop/1.jpeg
	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -fetch -uri file://C:/Users/shaow1/Desktop/2.jpeg
	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -fetch -uri http://www.baidu.com

### error: missing resourceTemplate (直接发JSON)

## EXCHANGE

### error: invalid command (直接发JSON)
### error: missing or incorrect type for command (直接发JSON)
### success

	java -jar client.jar -host 10.13.126.138 -port 20006 -debug -exchange -servers sunrise.cis.unimelb.edu.au:3781

### error: missing or invalid server list (直接发JSON)