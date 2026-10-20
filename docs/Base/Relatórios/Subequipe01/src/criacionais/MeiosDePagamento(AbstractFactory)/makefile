SRC_DIRS := dominio produto fabrica familias cliente
SOURCES  := $(foreach d,$(SRC_DIRS),$(shell find $(d) -name '*.java' 2>/dev/null))
OUT      := out
MAIN     := cliente.Main

.PHONY: all run gui filmar clean

all: $(OUT)/.compiled

$(OUT)/.compiled: $(SOURCES)
	mkdir -p $(OUT)
	javac -encoding UTF-8 -d $(OUT) $(SOURCES)
	touch $@

run: all
	java -cp $(OUT) $(MAIN)

gui: all
	java -cp $(OUT) cliente.Demonstracao

QUADROS := $(OUT)/quadros

filmar: all
	java -cp $(OUT) cliente.Demonstracao --filmar $(QUADROS)
	python3 ../../../Assets/src/video_demonstracao_abstract_factory.py \
		$(QUADROS) ../../../AbstractFactoryMeiosDePagamento/AbstractFactoryMeiosDePagamento.mp4

clean:
	rm -rf $(OUT)
