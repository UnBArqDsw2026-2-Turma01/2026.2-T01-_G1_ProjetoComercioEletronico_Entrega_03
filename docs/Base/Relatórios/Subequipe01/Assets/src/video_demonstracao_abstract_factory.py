#!/usr/bin/env python3
"""Monta o video da demonstracao grafica do GoF Criacional (Abstract Factory).

    AbstractFactoryMeiosDePagamento.mp4  -- a janela Swing percorrendo as quatro
                                            familias, os tres desfechos do cartao
                                            e a tentativa de misturar familias

Os quadros nao sao desenhados aqui: quem os pinta e a propria aplicacao, em
`cliente.Demonstracao --filmar`, que monta a janela de verdade, passa por
pack() para que o layout seja o mesmo da execucao interativa e grava um PNG
por estado do roteiro, junto de um tempos.txt com quantos segundos segurar
cada um. Este script so costura os quadros no ffmpeg.

A mesma janela roda interativamente com `make gui`: nada no video e encenado.

Autor: Patrick Anderson Carvalho dos Santos (demonstracao do criacional).

    make filmar                       grava os quadros em out/quadros
    python3 video_demonstracao_abstract_factory.py out/quadros saida.mp4
"""

import os
import subprocess
import sys

QUADROS, SAIDA = sys.argv[1], sys.argv[2]

tempos = [float(l) for l in open(os.path.join(QUADROS, 'tempos.txt')) if l.strip()]
pngs = sorted(p for p in os.listdir(QUADROS) if p.endswith('.png'))
if len(pngs) != len(tempos):
    raise SystemExit('quadros (%d) e tempos (%d) nao batem' % (len(pngs), len(tempos)))

lista = os.path.join(QUADROS, 'lista.txt')
with open(lista, 'w') as fh:
    for png, seg in zip(pngs, tempos):
        fh.write("file '%s'\nduration %.3f\n" % (png, seg))
    fh.write("file '%s'\n" % pngs[-1])

subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-f', 'concat', '-safe', '0',
                '-i', lista, '-vf', 'fps=15,format=yuv420p',
                '-c:v', 'libx264', '-preset', 'veryslow', '-tune', 'stillimage',
                '-crf', '24', '-g', '250', '-movflags', '+faststart', SAIDA], check=True)
print('quadros %d | duracao %.1fs | %s' % (len(pngs), sum(tempos), SAIDA))
