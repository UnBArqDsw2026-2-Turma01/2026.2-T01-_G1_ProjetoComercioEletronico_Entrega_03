SRC_DIRS := dominio produto fabrica familias cliente
SOURCES  := $(foreach d,$(SRC_DIRS),$(shell find $(d) -name '*.java' 2>/dev/null))
OUT      := out
MAIN     := cliente.Main

.PHONY: all run clean

all: $(OUT)/.compiled

$(OUT)/.compiled: $(SOURCES)
	mkdir -p $(OUT)
	javac -encoding UTF-8 -d $(OUT) $(SOURCES)
	touch $@

run: all
	java -cp $(OUT) $(MAIN)

clean:
	rm -rf $(OUT)
