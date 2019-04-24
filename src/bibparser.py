## Timothy Gwaltney, Camby Abell, and Jamie Rios
## CSCI 4130
## Filename: bibparser.py

## This program parses a bibliography bibtex file using python
## and the BibTexParser library.
##


import bibtexparser
import os
import sys
from bibtexparser.bparser import BibTexParser
import re

bib_index = os.path.dirname("user.dir")
parser = BibTexParser(common_strings=True)
file = open("output.txt", 'w')

for filename in os.listdir(os.path.join(os.getcwd(), bib_index)):
    if filename.endswith('.bib'):
        with open(filename, errors="ignore") as bibtex_file:
            bib_database = bibtexparser.load(bibtex_file, parser)

for i in bib_database.entries:
    file.write(str(i))
    file.write(str('\n'))

file.close()