SRC_DIRS := dominio componente decorador cliente
SOURCES  := $(foreach d,$(SRC_DIRS),$(wildcard $(d)/*.java))
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
