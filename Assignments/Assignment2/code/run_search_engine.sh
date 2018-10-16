#!/bin/sh
java -cp classes:pdfbox -Xmx1g ir.Engine -d /info/DD2476/ir18/lab/davisWiki -l ir18.jpg -p patterns.txt
