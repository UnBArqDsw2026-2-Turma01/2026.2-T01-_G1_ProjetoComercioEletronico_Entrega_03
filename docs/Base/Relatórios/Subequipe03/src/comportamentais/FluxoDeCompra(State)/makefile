SRC_DIRS := context state ui
SOURCES  := $(foreach d,$(SRC_DIRS),$(wildcard $(d)/*.java))
OUT      := out
MAIN     := ui.PedidoGUI

.PHONY: all run clean

all: $(OUT)/.compiled

$(OUT)/.compiled: $(SOURCES)
	mkdir -p $(OUT)
	javac -d $(OUT) $(SOURCES)
	touch $@

run: all
	java -cp $(OUT) $(MAIN)

clean:
	rm -rf $(OUT)