#!/usr/bin/env python

import os
import re  

def main():
  cleanup_puzzle('easy')
  cleanup_puzzle('simple')
  cleanup_puzzle('intermediate')
  cleanup_puzzle('expert')

def cleanup_puzzle(level):
  file = open('design/puzzles_%s.txt' % level, 'rb')
  buffer = ""
  with open('res/puzzles_%s.txt' % level, 'w') as f:
    for line in file:
      buffer += "".join(re.findall(r"[.0-9]", line))
      if len(buffer) == 81:
        f.write(buffer)
        f.write("\n")
        buffer = ""

if __name__ == "__main__":
    main()
