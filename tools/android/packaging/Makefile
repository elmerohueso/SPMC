include ../../depends/Makefile.include

OBJS = libcurl.so \
  librtmp.so  libmad.so \
  libafpclient.so  \
  libplist.so libshairplay.so \
  libxbogg.so libxbvorbis.so libxbvorbisfile.so libxbFLAC.so libxbmpeg2.so \
  libxbmpeg2convert.so libnfs.so libass.so libbluray.so

PLATFORM_OBJS =
EXCLUDED_ADDONS = screensaver.rsxs.euphoria visualization.dxspectrum visualization.milkdrop visualization.projectm

XBMCROOT = $(shell cd $(CURDIR)/../../..; pwd)
COPYDIRS = system addons language media
AAPT = $(SDKROOT)/platform-tools/aapt
DX = $(SDKROOT)/platform-tools/dx
GCC_VERSION=$(shell $(CC) -dumpversion)
ZIP=zip

SIGN_STORE=~/.android//spmc.keystore
SIGN_STOREPASS=android
SIGN_KEY=androiddebugkey
SIGN_KEYPASS=

ifneq ($(strip $(SIGN_KEYPASS)),)
SIGN_KEYPASS_OPT=-keypass $(SIGN_KEYPASS)
endif

X86OVERRIDES=XBMC_OVERRIDE_HOST=i686-android-linux XBMC_OVERRIDE_TOOLCHAIN=$(XBMC_X86_TOOLCHAIN)
ARMOVERRIDES=XBMC_OVERRIDE_HOST=arm-linux-androideabi XBMC_OVERRIDE_TOOLCHAIN=$(XBMC_ARM_TOOLCHAIN)

#this fixes a android ndk fuckup where the pathes to 
#prebuilt stuff follow different name shemes for
#arm and x86
ifeq ($(findstring i686,$(CPU)),i686)
ARCH=x86
CPU=x86
endif
ifeq ($(findstring arm,$(CPU)),arm)
ARCH=arm
endif

#older ndk x86
GDBPATH=$(NDKROOT)/toolchains/$(ARCH)-$(GCC_VERSION)/prebuilt/gdbserver

ifneq ($(shell test -f $(GDBPATH) && echo 1),1)
#older ndk arm
GDBPATH=$(NDKROOT)/toolchains/$(HOST)-$(GCC_VERSION)/prebuilt/gdbserver
endif

ifneq ($(shell test -f $(GDBPATH) && echo 1),1)
#vanilla ndk >= 8
GDBPATH=$(NDKROOT)/prebuilt/android-$(ARCH)/gdbserver/gdbserver
endif

SRCLIBS = $(addprefix $(PREFIX)/lib/,$(OBJS)) $(addprefix $(PREFIX)/lib/$(SDK_PLATFORM)/,$(PLATFORM_OBJS))
DSTLIBS = $(CPU)/lib/libxbmc.so $(addprefix $(CPU)/lib/,$(OBJS)) $(addprefix $(CPU)/lib/,$(PLATFORM_OBJS))
libs= $(DSTLIBS)

all: apk

apk: apk-clean sharedapk package apk-sign

obb: apk-clean sharedobb

apk-unsigned: apk-clean sharedapk package

apk-obb: apk-clean sharedobb package apk-sign

apk-obb-unsigned: apk-clean sharedobb package

apk-noobb: apk-clean package apk-sign

multi: x86 arm
	@cp images/xbmcapp-debug-skeleton.apk images/xbmcapp-debug-multi-unaligned.apk
	@cd xbmc; zip -r -q ../images/xbmcapp-debug-multi-unaligned.apk lib/ assets
	@jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore $(SIGN_STORE) -storepass $(SIGN_STOREPASS) $(SIGN_KEYPASS_OPT) images/xbmcapp-debug-multi-unaligned.apk $(SIGN_KEY)
	@$(SDKROOT)/tools/zipalign -f 4 images/xbmcapp-debug-multi-unaligned.apk $(XBMCROOT)/xbmcapp-multi-debug.apk
	@rm images/xbmcapp-debug-multi-unaligned.apk
	@echo "$(XBMCROOT)/xbmcapp-multi-debug.apk created"

xbmc/assets:
	mkdir -p xbmc/assets

shared:
	mkdir -p assets
	cp -rfp $(PREFIX)/share/xbmc/* ./assets
	find `pwd`/assets/ -depth -name ".git" -exec rm -rf {} \;
	find `pwd`/assets/system/ -name "*.so" -exec rm {} \;
	find `pwd`/assets/addons/skin.*/media/* -depth -not -iname "Textures.xbt" -exec rm -rf {} \;
	find `pwd`/assets/system/keymaps/ -depth -name "joystick*.xml" ! -name "joystick.xml" -exec rm {} \;
	cd assets; rm -rf $(EXCLUDED_ADDONS)

sharedapk: shared | xbmc/assets
	cp -rfp assets/* ./xbmc/assets

sharedobb: shared
	$(ZIP) -9 -q -r $(XBMCROOT)/main.xbmc.obb assets
	@echo "$(XBMCROOT)/main.xbmc.obb created"

python: | xbmc/assets
	mkdir -p xbmc/assets/python2.6/lib/
	cp -rfp $(PREFIX)/lib/python2.6 xbmc/assets/python2.6/lib/
	cd xbmc/assets/python2.6/lib/python2.6/; rm -rf test config lib-dynload

res:
	mkdir -p xbmc/res xbmc/res/raw images
	@echo "native_arch=$(ARCH)" > xbmc/res/raw/xbmc.properties
	cp -fp $(XBMCROOT)/media/Splash.png xbmc/res/drawable/splash.png
	cp -fp $(XBMCROOT)/media/Splash.png xbmc/res/drawable-xhdpi/ouya_icon.png
	cp -fp media/drawable-hdpi/ic_launcher.png xbmc/res/drawable-hdpi/ic_launcher.png
	cp -fp media/drawable-ldpi/ic_launcher.png xbmc/res/drawable-ldpi/ic_launcher.png
	cp -fp media/drawable-mdpi/ic_launcher.png xbmc/res/drawable-mdpi/ic_launcher.png
	cp -fp media/drawable-xhdpi/ic_launcher.png xbmc/res/drawable-xhdpi/ic_launcher.png
	cp -fp media/drawable-xxhdpi/ic_launcher.png xbmc/res/drawable-xxhdpi/ic_launcher.png
	mkdir -p tmp/res; $(AAPT) c -S xbmc/res -C tmp/res; cp -r -n xbmc/res tmp/ || true
	$(AAPT) p -f -I $(SDKROOT)/platforms/$(SDK_PLATFORM)/android.jar -S tmp/res/ -M xbmc/AndroidManifest.xml -F images/xbmcapp-debug-skeleton.apk -J xbmc/src
	@rm -rf tmp/

libs: $(PREFIX)/lib/xbmc/libxbmc.so
	rm -rf xbmc/lib/$(CPU) xbmc/libs/$(CPU) xbmc/obj/local/$(CPU)
	mkdir -p xbmc/lib/$(CPU) xbmc/assets/python2.6/lib/ xbmc/libs/$(CPU) xbmc/obj/local/$(CPU)
	cp -fp $(SRCLIBS) xbmc/obj/local/$(CPU)/
	cp -fp $(PREFIX)/lib/xbmc/libxbmc.so xbmc/obj/local/$(CPU)/
	find $(PREFIX)/lib/xbmc/addons -name "*.so" -exec cp -fp {} xbmc/obj/local/$(CPU)/ \;
	find $(PREFIX)/lib/xbmc/system -name "*.so" -exec cp -fp {} xbmc/obj/local/$(CPU)/ \;
	cd xbmc/obj/local/$(CPU)/; find . -name "*.so" -not -name "lib*.so" | sed "s/\.\///" | xargs -I@ mv @ lib@
	cp -fp xbmc/obj/local/$(CPU)/*.so xbmc/lib/$(CPU)/
	$(STRIP) --strip-unneeded xbmc/lib/$(CPU)/*.so
	install -p $(GDBPATH) ./xbmc/libs/$(CPU)/gdbserver
	echo "set solib-search-path ./obj/local/$(CPU)" > ./xbmc/libs/$(CPU)/gdb.setup
	echo "directory $(TOOLCHAIN)/sysroot/usr/include $(NDKROOT)/sources/android/native_app_glue" \
	     "$(NDKROOT)/sources/cxx-stl/gnu-libstdc++/$(GCC_VERSION)/include $(XBMCROOT)  $(PREFIX)/include jni" >> ./xbmc/libs/$(CPU)/gdb.setup
	cp -fp xbmc/libs/$(CPU)/* xbmc/lib/$(CPU)

xbmc/classes.dex: res
	mkdir -p xbmc/obj
	@javac -classpath $(SDKROOT)/platforms/$(SDK_PLATFORM)/android.jar:xbmc/obj -d xbmc/obj -sourcepath xbmc/src xbmc/src/*.java
	@javac -classpath $(SDKROOT)/platforms/$(SDK_PLATFORM)/android.jar:xbmc/obj -d xbmc/obj -sourcepath xbmc/src xbmc/src/org/xbmc/xbmc/*.java
	@$(DX) --dex --output=xbmc/classes.dex xbmc/obj

package: libs python xbmc/classes.dex
	@echo "Creating package..."
	@cp images/xbmcapp-debug-skeleton.apk images/xbmcapp-debug-$(CPU)-unaligned.apk
	@cd xbmc; $(ZIP) -r -q ../images/xbmcapp-debug-$(CPU)-unaligned.apk lib/$(CPU) assets classes.dex

apk-sign:
	@echo "Signing..."
	@jarsigner -sigalg MD5withRSA -digestalg SHA1 -keystore $(SIGN_STORE) -storepass $(SIGN_STOREPASS) $(SIGN_KEYPASS_OPT) images/xbmcapp-debug-$(CPU)-unaligned.apk $(SIGN_KEY)
	@$(SDKROOT)/tools/zipalign -f 4 images/xbmcapp-debug-$(CPU)-unaligned.apk $(XBMCROOT)/spmc-$(CPU)-debug.apk
	@echo "$(XBMCROOT)/spmc-$(CPU)-debug.apk created"

$(PREFIX)/lib/xbmc/libxbmc.so: $(SRCLIBS)
	$(MAKE) -C ../../depends/target/xbmc

$(SRCLIBS):

arm: armeabi-v7a

armeabi-v7a: $(XBMC_ARM_TOOLCHAIN) force
	XBMC_OVERRIDE_PLATFORM=$@ $(ARMOVERRIDES) $(MAKE) package

x86: $(XBMC_X86_TOOLCHAIN) force
	XBMC_OVERRIDE_PLATFORM=$@ $(X86OVERRIDES) $(MAKE) package

apk-clean:
	rm -rf images
	rm -rf xbmc/lib
	rm -rf xbmc/libs
	rm -rf xbmc/assets
	rm -rf xbmc/obj
	rm -rf xbmc/res/raw
	rm -rf tmp
	rm -f xbmc/res/drawable/splash.png
	rm -f xbmc/src/R.java
	rm -f xbmc/classes.dex
	rm -rf assets

.PHONY: arm armeabi-v7a x86 force libs assets python sharedapk sharedobb res package

