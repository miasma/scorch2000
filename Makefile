#Scorch Server make file
#by Alexander Rasin
#

.SUFFIXES: .java .class
FLAGS = -classpath Scorch.jar:/home/p/demo/demo/users/phase/jdk/lib/classes.zip:.

.java.class:
	/home/p/demo/demo/users/phase/jdk/bin/javac $(FLAGS) $<

all:	server shell

server: 	\
	ScorchServer/ScorchServer.class 	\
	ScorchServer/AIPlayer.class		\
	ScorchServer/Disk.class			\
	ScorchServer/Game.class			\
	ScorchServer/Player.class		\
	ScorchServer/ServerThread.class		
#	ScorchServer/Protocol.class		\

shell:		\
	ScorchServer/ServerShell/ServerShell.class		\
	ScorchServer/ServerShell/RemoteServerShell.class	\
	ScorchServer/ServerShell/commands/shellCommand.class	\
	ScorchServer/ServerShell/commands/lg.class		\
	ScorchServer/ServerShell/commands/boot.class		\
	ScorchServer/ServerShell/commands/help.class		\
	ScorchServer/ServerShell/commands/desync.class		\
	ScorchServer/ServerShell/commands/whois.class		\
	ScorchServer/ServerShell/commands/passwd.class		\
	ScorchServer/ServerShell/commands/shout.class		\
	ScorchServer/ServerShell/commands/addkg.class		\
	ScorchServer/ServerShell/commands/shutdown.class	\
	ScorchServer/ServerShell/commands/say.class		
#	ScorchServer/ServerShell/commands/ServerShell.class

run:
	java -Djava.compiler= -cp Scorch.jar:ScorchServer.jar: ScorchServer.ScorchServer 4242

install: 
	cp ScorchServer.jar ~/cs120a/development/;
	scp ScorchServer.jar scorch@maxho.com:~/public_html/


shrun:
	java ScorchServer.ServerShell.ServerShell

crun:	
	appletviewer 800x600.html &
	appletviewer 800x600.html &
debug:	
	appletviewer dev.html &
	appletviewer dev.html &
jar:
	jar cvf ScorchServer.jar ScorchServer/*.class ScorchServer/ServerShell/*.class ScorchServer/ServerShell/commands/*.class

clean:
	find . -name '*class' -print0 | xargs -0r rm -f     
	find . -name '*~'   -print0 | xargs -0r rm -f    
#	rm -f *~ *\# *.class */*.class ScorchServer/ServerShell/*.class
#	rm -f ScorchServer/ServerShell/*~ ScorchServer/ServerShell/commands/*~
#	rm -f  WS_FTP.LOG */*~ ScorchServer/ServerShell/commands/*.class





