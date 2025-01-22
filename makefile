
# Make run does basic benchmarking.

# Make runflame will produce profiling flamegraphs, as long as a
# working libasyncProfiler.so gets downloaded.

COUNT = 10_000_000
ORDER = 10

XMOC_SRC = xmoc/ArraysMoc.java \
           xmoc/BitSetBag.java \
           xmoc/IndexBag.java \
           xmoc/IndexBags.java \
           xmoc/LongBag.java \
           xmoc/MocBuilder.java \
           xmoc/PrimitiveList.java \
           xmoc/LongList.java \

run: build
	java -jar xmocbench.jar -order $(ORDER) -count $(COUNT)
	@echo
	java -jar mocbench1.jar -order $(ORDER) -count $(COUNT)
	@echo
	java -jar mocbench2.jar -order $(ORDER) -count $(COUNT)

runflame: profile1.html profile2.html

profile1.html: libasyncProfiler.so mocbench1.jar -order $(ORDER) -count $(COUNT)
	java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints \
             -agentpath:./libasyncProfiler.so=start,file=$@ \
             -jar mocbench1.jar

profile2.html: libasyncProfiler.so mocbench2.jar -order $(ORDER) -count $(COUNT)
	java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints \
             -agentpath:./libasyncProfiler.so=start,file=$@ \
             -jar mocbench2.jar

build: mocbench1.jar mocbench2.jar xmocbench.jar

JavaMoc2.jar:
	curl -O https://wiki.ivoa.net/internal/IVOA/MocInfo/JavaMoc2.jar

Moc.jar:
	curl -O https://wiki.ivoa.net/internal/IVOA/MocInfo/Moc.jar

libasyncProfiler.so:
	curl -L https://github.com/async-profiler/async-profiler/releases/download/v3.0/async-profiler-3.0-linux-x64.tar.gz | tar zxO async-profiler-3.0-linux-x64/lib/libasyncProfiler.so >$@

mocbench1.jar: MocBench.java HealpixMocBench.java Moc.jar
	rm -rf tmp
	mkdir -p tmp
	javac -classpath Moc.jar MocBench.java HealpixMocBench.java -d tmp
	cd tmp; jar xf ../Moc.jar; jar cfe ../$@ HealpixMocBench .
	rm -rf tmp

mocbench2.jar: MocBench.java SMocBench.java JavaMoc2.jar
	rm -rf tmp
	mkdir -p tmp
	javac -classpath JavaMoc2.jar MocBench.java SMocBench.java -d tmp
	cd tmp; jar xf ../JavaMoc2.jar; jar cfe ../$@ SMocBench .
	rm -rf tmp

xmocbench.jar: MocBench.java XMocBench.java xmoc.jar
	rm -rf tmp
	mkdir -p tmp
	javac -classpath xmoc.jar MocBench.java XMocBench.java -d tmp
	cd tmp; jar xf ../xmoc.jar; jar cfe ../$@ XMocBench .
	rm -rf tmp

xmoc.jar: $(XMOC_SRC)
	rm -rf tmp
	mkdir -p tmp
	javac $(XMOC_SRC) -d tmp
	cd tmp; jar cf ../$@ .
	rm -rf tmp


clean:
	rm -rf tmp
	rm -f mocbench1.jar mocbench2.jar xmocbench.jar
	rm -f xmoc.jar
	rm -f profile1.html profile2.html
	rm -f HealpixMocBench.fits SMocBench.fits

veryclean: clean
	rm -f Moc.jar JavaMoc2.jar libasyncProfiler.so

