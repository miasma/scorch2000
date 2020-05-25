#
# Makefile for Scorched Earth 2000
# this is temporary version, it does not reflect any dependencies
# hopefully this will function in the simplest cases
# written by Mikhail Kruk, 29/02/00
#

.SUFFIXES: .java .class
#FLAGS = -classpath ScorchServer.jar;.
FLAGS = 
#-deprecation
JLOC = /usr/local/jdk1.1.8/bin/
#JLOC = 
SVER = 1061

.java.class:
	$(JLOC)javac $(FLAGS) $<

all:	scorch swindows utility gui

scorch:	\
	scorch/ScorchApplet.class \
	scorch/Audible.class	\
	scorch/Physics.class	\
	scorch/Bitmap.class \
	scorch/PlayerProfile.class \
	scorch/Dropper.class \
	scorch/PlayerSettings.class \
	scorch/GameSettings.class \
	scorch/ScorchColorModel.class \
	scorch/ScorchField.class \
		scorch/Tooltip.class \
	scorch/ScorchChat.class \
	scorch/ScorchPlayer.class \
	scorch/AIPlayer.class \
	scorch/Network.class \
	scorch/Tanks.class \
	scorch/PhysicalObject.class \
	scorch/ChatScreen.class \
	scorch/dRandom.class \
	scorch/GradientPanel.class \
	scorch/Explodable.class \
\
	scorch/weapons/BabyNuke.class	\
	scorch/weapons/Explosion.class \
	scorch/weapons/Directional.class \
	scorch/weapons/ParticlesExplosion.class \
	scorch/weapons/RoundExplosion.class \
	scorch/weapons/SandBomb.class \
	scorch/weapons/ExplosionInfo.class \
	scorch/weapons/SandExplosion.class \
	scorch/weapons/FireExplosion.class \
		scorch/weapons/FireColorModel.class \
	scorch/weapons/GenericMissile.class \
	scorch/weapons/RoundMissile.class \
	scorch/weapons/MIRVMissile.class \
	scorch/weapons/TracerMissile.class \
	scorch/weapons/LaserExplosion.class \
		scorch/weapons/LaserColorModel.class \
	scorch/weapons/SimpleExplosion.class \
	scorch/weapons/GradientExplosion.class \
	scorch/weapons/RollerExplosion.class \
	scorch/weapons/Roller.class \
	scorch/weapons/BabyRoller.class \
	scorch/weapons/HeavyRoller.class \
	scorch/weapons/DiggerExplosion.class \
	scorch/weapons/BabyDigger.class \
	scorch/weapons/Digger.class \
	scorch/weapons/HeavyDigger.class \
	scorch/weapons/NapalmExplosion.class \
		scorch/weapons/NapalmLine.class \
		scorch/weapons/NapalmColorModel.class \
	scorch/weapons/Napalm.class \
	scorch/weapons/HotNapalm.class \
	scorch/weapons/Missile.class \
	scorch/weapons/Nuke.class \
	scorch/weapons/MIRV.class \
	scorch/weapons/DeathHead.class \
	scorch/weapons/MIRVExplosion.class \
	scorch/weapons/FunkyBomb.class \
	scorch/weapons/FunkyNuke.class \
	scorch/weapons/FunkyExplosion.class \
		scorch/weapons/ColorStripExplosion.class \
	scorch/weapons/Weapon.class \
		scorch/weapons/WeaponControl.class \
\
	scorch/backgrounds/PlainBackground.class \
	scorch/backgrounds/Background.class \
	scorch/backgrounds/GradientBackground.class \
	scorch/backgrounds/StarsBackground.class \
\
	scorch/items/Item.class \
	scorch/items/ItemControl.class \
	scorch/items/Shield.class \
		scorch/items/ShieldControl.class \
	scorch/items/MediumShield.class \
	scorch/items/HeavyShield.class \
	scorch/items/Tracer.class \
	 	scorch/items/TracerControl.class \
	scorch/items/Parachute.class \
	scorch/items/AutoDefense.class \
		scorch/items/AutoDefenseControl.class \
	scorch/items/Battery.class \
		scorch/items/BatteryControl.class \
	scorch/items/Fuel.class \
		scorch/items/FuelControl.class

gui: \
	scorch/gui/AboutBox.class \
	scorch/gui/PlayersLister.class \
	scorch/gui/ChatBox.class \
	scorch/gui/BootBox.class \
	scorch/gui/ScorchFrame.class \
	scorch/gui/CreditsPanel.class \
	scorch/gui/GameOptions.class \
	scorch/gui/ShopWindow.class \
	scorch/gui/Inventory.class \
	scorch/gui/AutoDefenseWnd.class \
	scorch/gui/StartGame.class \
	scorch/gui/JoinGame.class \
	scorch/gui/StatsWindow.class \
	scorch/gui/LoginWindow.class \
	scorch/gui/BannerWindow.class \
		scorch/gui/Banner.class \
	scorch/gui/SystemMenu.class \
	scorch/gui/MainToolbar.class \
	scorch/gui/TankSelection.class \
		scorch/gui/TankSelectionControl.class \
	scorch/gui/NewUser.class \
	scorch/gui/FuelBox.class \
	scorch/gui/PlayersList.class \
	scorch/gui/PlayersListControl.class \
	scorch/gui/ChatPanel.class \
	scorch/gui/UsersPanel.class \
	scorch/gui/GameControl.class \
	scorch/gui/GamesPanel.class

swindows: \
	swindows/MessageBox.class \
	swindows/sGauge.class \
	swindows/sScrollPanel.class \
	swindows/sWindow.class \
	swindows/sPanel.class \
	swindows/sScrollPanel.class \
		swindows/sScrollBar.class \
		swindows/sScrollButton.class

utility: \
	scorch/utility/Debug.class \
		scorch/utility/Console.class \
	scorch/utility/QSort.class \
	scorch/utility/Crypt.class

runserver:
	$(JLOC)java -classpath Scorch.jar:ScorchServer.jar ScorchServer.ScorchServer

run:
	$(JLOC)appletviewer 800x600.html	

clean:
	rm -f *class *~ scorch/*class scorch/*~
	rm -f scorch/items/*class scorch/items/*~
	rm -f scorch/weapons/*class scorch/weapons/*~
	rm -f scorch/backgrounds/*class scorch/backgrounds/*~
	rm -f scorch/gui/*class scorch/gui/*~
	rm -f swindows/*class swindows/*~
	rm -f scorch/utility/*class scorch/utility/*~
	rm -f *~

clean_all: clean
	rm -f ScorchServer/*class ScorchServer/*~

jar:
	$(JLOC)jar cvf work/Scorch.jar scorch/*/*class scorch/*class swindows/*class

install:
	scp Scorch.jar scorch@scorch2000.com:/home/scorch/public_html/

snap:
	/usr/bin/tar zcvf snapshots/scorch-`date "+%d-%m-%Y"`.tar.gz */*java */*/*java Makefile

distro:
	mkdir scorch$(SVER)
	cp -r Sound scorch$(SVER)/
	cp 800x600.html scorch$(SVER)/
	cp 1024x768.html scorch$(SVER)/
	cp COPYING scorch$(SVER)/
	cp README scorch$(SVER)/
	cp rs.bat scorch$(SVER)/
	cp rs scorch$(SVER)/
	cp rs.kaffe scorch$(SVER)/
	cp Scorch.jar scorch$(SVER)/
	cp ScorchServer.jar scorch$(SVER)/
	touch scorch$(SVER)/players.db
	tar cvf scorch$(SVER).tar scorch$(SVER)/
	bzip2 scorch$(SVER).tar
	zip -r scorch$(SVER).zip scorch$(SVER)

	cp Makefile scorch$(SVER)/
	cp -r scorch scorch$(SVER)/
	cp -r swindows scorch$(SVER)/
	mv scorch$(SVER) scorch$(SVER)src
	tar cvf scorch$(SVER)src.tar scorch$(SVER)src/
	bzip2 scorch$(SVER)src.tar
	zip -r scorch$(SVER)src.zip scorch$(SVER)src

	rm -rf scorch$(SVER)src