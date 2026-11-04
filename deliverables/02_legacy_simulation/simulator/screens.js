const SCREENS = {
 "BNK1ACC": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1ACC",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 14,
   "len": 63,
   "initial": "CICS Banking Sample Application - Accounts for  Customers.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 44,
   "initial": "Provide a Customer number. Then press Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 1,
   "len": 15,
   "initial": "CUSTOMER NUMBER",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "CUSTNO",
   "row": 5,
   "col": 17,
   "len": 10,
   "initial": "",
   "attrb": "NORM,NUM,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 5,
   "col": 28,
   "len": 1,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 41,
   "initial": "SORT CODE   ACCOUNT NUMBER   ACCOUNT TYPE",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 43,
   "len": 33,
   "initial": "   AVAIL BALANCE   ACTUAL BALANCE",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACCOUNT",
   "row": 9,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT,ASKIP",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT,ASKIP",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,ASKIP,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1B2M": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1B2",
   "attrb": "ASKIP,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 15,
   "len": 58,
   "initial": "CICS Banking Sample Application - Transfer Fund s",
   "attrb": "PROT,NORM",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 15,
   "initial": "Transfer funds",
   "attrb": "ASKIP,NORM",
   "color": "NEUTRAL",
   "hilight": "OFF"
  },
  {
   "name": "",
   "row": 2,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 5,
   "len": 73,
   "initial": "Provide the Sort Code and Account Number of the Accounts you want ",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 6,
   "col": 5,
   "len": 68,
   "initial": "to transfer money FROM and TO and the AMOUNT yo u want to transfer. ",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 5,
   "len": 68,
   "initial": "Then press ENTER.  ",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": "OFF"
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 5,
   "len": 12,
   "initial": "From Account",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": "OFF"
  },
  {
   "name": "",
   "row": 10,
   "col": 10,
   "len": 16,
   "initial": "Sort Code      :",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FSCDE1",
   "row": 10,
   "col": 27,
   "len": 2,
   "initial": "00",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 30,
   "len": 1,
   "initial": "-",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FSCDE2",
   "row": 10,
   "col": 32,
   "len": 2,
   "initial": "00",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 35,
   "len": 1,
   "initial": "-",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FSCDE3",
   "row": 10,
   "col": 37,
   "len": 2,
   "initial": "00",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 40,
   "len": 39,
   "initial": "",
   "attrb": "",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 10,
   "len": 16,
   "initial": "Account Number :",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "FACCNO",
   "row": 11,
   "col": 27,
   "len": 8,
   "initial": "",
   "attrb": "NUM,BRT,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 11,
   "col": 36,
   "len": 43,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 10,
   "len": 26,
   "initial": "Amount to be Transferred :",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "AMT",
   "row": 13,
   "col": 39,
   "len": 13,
   "initial": "0000000000.00",
   "attrb": "NORM,UNPROT",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 13,
   "col": 53,
   "len": 24,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 10,
   "len": 26,
   "initial": "Actual Balance ......... :",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACTSIGN",
   "row": 15,
   "col": 37,
   "len": 1,
   "initial": " ",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACTPND",
   "row": 15,
   "col": 39,
   "len": 10,
   "initial": "0000000000",
   "attrb": "NUM,PROT,ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 50,
   "len": 1,
   "initial": ".",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACTPNC",
   "row": 15,
   "col": 52,
   "len": 2,
   "initial": "00",
   "attrb": "NUM,PROT,ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 55,
   "len": 24,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 10,
   "len": 26,
   "initial": "Available Balance ...... :",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "AVASIGN",
   "row": 16,
   "col": 37,
   "len": 1,
   "initial": " ",
   "attrb": "ASKIP,NORM",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "AVAPND",
   "row": 16,
   "col": 39,
   "len": 10,
   "initial": "0000000000",
   "attrb": "NUM,NORM,PROT,ASKIP",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 50,
   "len": 1,
   "initial": ".",
   "attrb": "ASKIP,NORM,PROT,ASKIP",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "AVAPNC",
   "row": 16,
   "col": 52,
   "len": 2,
   "initial": "00",
   "attrb": "NUM,NORM,PROT,ASKIP",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 55,
   "len": 24,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 18,
   "col": 5,
   "len": 12,
   "initial": "To Account",
   "attrb": "ASKIP,BRT",
   "color": "YELLOW",
   "hilight": "OFF"
  },
  {
   "name": "",
   "row": 19,
   "col": 10,
   "len": 16,
   "initial": "Sort Code      :",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "TSCDE1",
   "row": 19,
   "col": 27,
   "len": 2,
   "initial": "00",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 30,
   "len": 1,
   "initial": "-",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "TSCDE2",
   "row": 19,
   "col": 32,
   "len": 2,
   "initial": "00",
   "attrb": "ASKIP,BRT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 35,
   "len": 1,
   "initial": "-",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "TSCDE3",
   "row": 19,
   "col": 37,
   "len": 2,
   "initial": "00",
   "attrb": "NUM,BRT",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 19,
   "col": 40,
   "len": 39,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 20,
   "col": 10,
   "len": 16,
   "initial": "Account Number :",
   "attrb": "ASKIP,NORM",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "TACCNO",
   "row": 20,
   "col": 27,
   "len": 8,
   "initial": "",
   "attrb": "NUM,BRT",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 20,
   "col": 36,
   "len": 43,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 21,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 22,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "ASKIP,BRT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,ASKIP",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,ASKIP",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1CAM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1CA ",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 18,
   "len": 61,
   "initial": "CICS Banking Sample Application- Create Account .",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 57,
   "initial": "Please provide the requested information and pr ess Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 6,
   "col": 1,
   "len": 18,
   "initial": "Customer number  :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "CUSTNO",
   "row": 6,
   "col": 23,
   "len": 10,
   "initial": "",
   "attrb": "NORM,NUM,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 6,
   "col": 34,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 1,
   "len": 18,
   "initial": "Account Type     :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACCTYP",
   "row": 7,
   "col": 23,
   "len": 8,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 7,
   "col": 32,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 18,
   "initial": "Interest Rate    :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "INTRT",
   "row": 8,
   "col": 23,
   "len": 7,
   "initial": "0000.00",
   "attrb": "NORM,NUM,FSET,UNPROT",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 31,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 1,
   "len": 18,
   "initial": "Overdraft Limit  :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "OVERDR",
   "row": 9,
   "col": 23,
   "len": 8,
   "initial": "0",
   "attrb": "NORM,NUM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 9,
   "col": 32,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 18,
   "initial": "Account number   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACCNO",
   "row": 13,
   "col": 23,
   "len": 8,
   "initial": "        ",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 18,
   "initial": "Sort code        :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SRTCD",
   "row": 14,
   "col": 23,
   "len": 6,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 1,
   "len": 18,
   "initial": "Account Opened   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OPENDD",
   "row": 15,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OPENMM",
   "row": 15,
   "col": 28,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 31,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OPENYY",
   "row": 15,
   "col": 33,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 1,
   "len": 18,
   "initial": "Last Stmt Date   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "LSTMDD",
   "row": 16,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "LSTMMM",
   "row": 16,
   "col": 28,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 31,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "LSTMYY",
   "row": 16,
   "col": 33,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 18,
   "initial": "Next Stmt Date   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTDD",
   "row": 17,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTMM",
   "row": 17,
   "col": 28,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 31,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTYY",
   "row": 17,
   "col": 33,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 18,
   "col": 1,
   "len": 18,
   "initial": "Available Balance:",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "AVAIL",
   "row": 18,
   "col": 22,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 1,
   "len": 18,
   "initial": "Actual Balance   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACTBAL",
   "row": 19,
   "col": 22,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1CCM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1CC ",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 16,
   "len": 55,
   "initial": "CICS Banking Sample Application - Create Custom er.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 46,
   "initial": "Provide a Name, Address and D.O.B. then Enter ",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 1,
   "len": 16,
   "initial": " Customer Title",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTTIT",
   "row": 7,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 7,
   "col": 29,
   "len": 0,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 31,
   "len": 14,
   "initial": "First Name",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CHRISTN",
   "row": 7,
   "col": 46,
   "len": 20,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 7,
   "col": 67,
   "len": 0,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 16,
   "initial": " Middle Initials",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTINS",
   "row": 8,
   "col": 18,
   "len": 2,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 21,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 22,
   "len": 12,
   "initial": "Family name ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTSN",
   "row": 8,
   "col": 35,
   "len": 20,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 56,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 1,
   "len": 16,
   "initial": " Address Line 1 ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTAD1",
   "row": 10,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 10,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 16,
   "initial": " Address Line 2 ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTAD2",
   "row": 11,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 11,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 16,
   "initial": " City           ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CITY",
   "row": 12,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 12,
   "col": 69,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 16,
   "initial": " Postcode       ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "POSTCODE",
   "row": 13,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 13,
   "col": 29,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 16,
   "initial": " Country        ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "COUNTRY",
   "row": 14,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 14,
   "col": 69,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 1,
   "len": 16,
   "initial": " Customer D.O.B.",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBDD",
   "row": 15,
   "col": 18,
   "len": 2,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET,NUM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 15,
   "col": 21,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBMM",
   "row": 15,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET,NUM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 15,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBYY",
   "row": 15,
   "col": 28,
   "len": 4,
   "initial": "",
   "attrb": "NORM,NUM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 15,
   "col": 33,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 16,
   "initial": " Sort Code     ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SORTC",
   "row": 17,
   "col": 18,
   "len": 6,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 18,
   "col": 1,
   "len": 16,
   "initial": " Customer Number",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTNO2",
   "row": 18,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 1,
   "len": 16,
   "initial": " Credit Score   ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CREDSC",
   "row": 19,
   "col": 18,
   "len": 3,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 20,
   "col": 1,
   "len": 16,
   "initial": " CS Review Date ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTDD",
   "row": 20,
   "col": 18,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 20,
   "col": 21,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTMM",
   "row": 20,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 20,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTYY",
   "row": 20,
   "col": 28,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,NUM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1CDM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1CD ",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 15,
   "len": 61,
   "initial": "CICS Banking Sample Application - Credit/Debit  Funds.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 61,
   "initial": "Provide an ACCOUNT number and an AMOUNT and the n press Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 15,
   "initial": "ACCOUNT NUMBER:",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACCNO",
   "row": 8,
   "col": 17,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,IC,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 26,
   "len": 7,
   "initial": "AMOUNT:",
   "attrb": "NORM,PROT,ASKIP",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "SIGN",
   "row": 8,
   "col": 35,
   "len": 1,
   "initial": "+",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "AMT",
   "row": 8,
   "col": 37,
   "len": 13,
   "initial": "0000000000.00",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 51,
   "len": 1,
   "initial": " ",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 1,
   "len": 16,
   "initial": "Sort Code: ",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "SORTC",
   "row": 10,
   "col": 21,
   "len": 6,
   "initial": "      ",
   "attrb": "NORM,NUM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 18,
   "initial": "Available Balance:",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "AVBAL",
   "row": 11,
   "col": 20,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 16,
   "initial": "Actual Balance:",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACTBAL",
   "row": 12,
   "col": 20,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1DAM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1DA ",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 16,
   "len": 55,
   "initial": "CICS Banking Sample Application - Display Accou nt.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 44,
   "initial": "Provide an ACCOUNT number. Then press Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 1,
   "len": 15,
   "initial": "ACCOUNT NUMBER",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACCNO",
   "row": 5,
   "col": 17,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 5,
   "col": 26,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 1,
   "len": 18,
   "initial": " Customer Number:",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTNO",
   "row": 7,
   "col": 20,
   "len": 10,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 18,
   "initial": " Sort Code      :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SORTC",
   "row": 8,
   "col": 20,
   "len": 6,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 1,
   "len": 18,
   "initial": " Account Number :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACCNO2",
   "row": 9,
   "col": 20,
   "len": 10,
   "initial": "",
   "attrb": "PROT,ASKIP,FSET,NORM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 1,
   "len": 18,
   "initial": " Account Type   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACTYPE",
   "row": 10,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "PROT,ASKIP,FSET,NORM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 18,
   "initial": " Interest Rate  :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "INTRT",
   "row": 11,
   "col": 20,
   "len": 7,
   "initial": "",
   "attrb": "PROT,ASKIP,FSET,NORM",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 18,
   "initial": " Account Opened :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OPENDD",
   "row": 12,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "OPENMM",
   "row": 12,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "OPENYY",
   "row": 12,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 18,
   "initial": " Overdraft limit:",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OVERDR",
   "row": 13,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 18,
   "initial": " Last statement :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "LSTMTDD",
   "row": 14,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "LSTMTMM",
   "row": 14,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "LSTMTYY",
   "row": 14,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 1,
   "len": 18,
   "initial": " Next statement :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTDD",
   "row": 15,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "NSTMTMM",
   "row": 15,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "NSTMTYY",
   "row": 15,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 1,
   "len": 17,
   "initial": " Available Bal  :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "AVBAL",
   "row": 16,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 17,
   "initial": " Actual Balance :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACTBAL",
   "row": 17,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT,ASKIP",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT,ASKIP",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET,ASKIP",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1DCM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1DC ",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 16,
   "len": 55,
   "initial": "CICS Banking Sample Application - Display Custo mer.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 44,
   "initial": "Provide a CUSTOMER number. Then press Enter.",
   "attrb": "ASKIP,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 1,
   "len": 16,
   "initial": "CUSTOMER NUMBER",
   "attrb": "ASKIP,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "CUSTNO",
   "row": 5,
   "col": 17,
   "len": 10,
   "initial": "",
   "attrb": "NORM,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 5,
   "col": 28,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 1,
   "len": 16,
   "initial": "Sort Code       ",
   "attrb": "ASKIP,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SORTC",
   "row": 7,
   "col": 18,
   "len": 6,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 16,
   "initial": "Customer Number ",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTNO2",
   "row": 8,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 1,
   "len": 16,
   "initial": "Title           ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTTITL",
   "row": 9,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "PROT,FSET,NORM,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 29,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 1,
   "len": 16,
   "initial": "First Name      ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTFNAM",
   "row": 10,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "PROT,FSET,NORM,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 16,
   "initial": "Last Name       ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTLNAM",
   "row": 11,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "PROT,FSET,NORM,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 16,
   "initial": "Address Line 1  ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTAD1",
   "row": 12,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "PROT,FSET,NORM,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 16,
   "initial": "Address Line 2  ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTAD2",
   "row": 13,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP,FSET",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 16,
   "initial": "City            ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTCITY",
   "row": 14,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 1,
   "len": 16,
   "initial": "Postcode        ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTPOST",
   "row": 15,
   "col": 18,
   "len": 10,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 29,
   "len": 0,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 1,
   "len": 16,
   "initial": "Country         ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTCTRY",
   "row": 16,
   "col": 18,
   "len": 50,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 69,
   "len": 0,
   "initial": "",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 16,
   "initial": "Customer D.O.B.",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBDD",
   "row": 17,
   "col": 18,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 21,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBMM",
   "row": 17,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "DOBYY",
   "row": 17,
   "col": 28,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 33,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 18,
   "col": 1,
   "len": 17,
   "initial": "Credit Score    ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CREDSC",
   "row": 18,
   "col": 18,
   "len": 3,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 1,
   "len": 17,
   "initial": "CS Review Date  ",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTDD",
   "row": 19,
   "col": 18,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 21,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTMM",
   "row": 19,
   "col": 23,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 19,
   "col": 26,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SCRDTYY",
   "row": 19,
   "col": 28,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT,ASKIP",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT,ASKIP",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET,ASKIP",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1MAI": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1MA ",
   "attrb": "PROT,NORM",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 20,
   "len": 43,
   "initial": "CICS Banking Sample Application - Main Menu.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 36,
   "initial": "Select an option. Then press Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 1,
   "len": 14,
   "initial": "Action . . . .",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACTION",
   "row": 5,
   "col": 16,
   "len": 1,
   "initial": " ",
   "attrb": "NORM,UNPROT,IC",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 5,
   "col": 18,
   "len": 46,
   "initial": "1.  Display/Delete/Update CUSTOMER information",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 6,
   "col": 18,
   "len": 38,
   "initial": "2.  Display/Delete ACCOUNT information",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 18,
   "len": 19,
   "initial": "3.  Create CUSTOMER",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 18,
   "len": 18,
   "initial": "4.  Create ACCOUNT",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 18,
   "len": 18,
   "initial": "5.  Update ACCOUNT",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 18,
   "len": 36,
   "initial": "6.  Credit/Debit funds to an ACCOUNT",
   "attrb": "ASKIP,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 18,
   "len": 42,
   "initial": "7.  Transfer funds",
   "attrb": "PROT,ASKIP",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 18,
   "len": 41,
   "initial": "A.  Look up Accounts with Customer Number",
   "attrb": "ASKIP,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT,ASKIP",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT,ASKIP",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET,ASKIP",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1TFM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1TF ",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 17,
   "len": 61,
   "initial": "CICS Banking Sample Application - Transfer fund s.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 70,
   "initial": "Provide a FROM account, a TO account and an Amo unt and press Enter.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 21,
   "initial": "FROM Account Number:",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FACCNO",
   "row": 8,
   "col": 23,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 32,
   "len": 19,
   "initial": "TO Account Number:",
   "attrb": "NORM,PROT,ASKIP",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "TACCNO",
   "row": 8,
   "col": 52,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 8,
   "col": 61,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 1,
   "len": 7,
   "initial": "AMOUNT:",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "AMT",
   "row": 9,
   "col": 23,
   "len": 13,
   "initial": "0000000000.00",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 9,
   "col": 37,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 16,
   "initial": "FROM Account   :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FACCNO2",
   "row": 11,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 35,
   "len": 16,
   "initial": "TO Account     :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "TACCNO2",
   "row": 11,
   "col": 55,
   "len": 8,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 16,
   "initial": "Sort Code      :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FSORTC",
   "row": 12,
   "col": 20,
   "len": 6,
   "initial": "      ",
   "attrb": "NORM,NUM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 35,
   "len": 16,
   "initial": "Sort Code      :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "TSORTC",
   "row": 12,
   "col": 55,
   "len": 6,
   "initial": "      ",
   "attrb": "NORM,NUM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 16,
   "initial": "Actual Balance :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FACTBAL",
   "row": 13,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 35,
   "len": 16,
   "initial": "Actual Balance :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "TACTBAL",
   "row": 13,
   "col": 54,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 16,
   "initial": "Avail Balance  :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "FAVBAL",
   "row": 14,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 35,
   "len": 18,
   "initial": "Avail Balance  :",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "TAVBAL",
   "row": 14,
   "col": 54,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ],
 "BNK1UAM": [
  {
   "name": "",
   "row": 1,
   "col": 1,
   "len": 7,
   "initial": "BNK1UA ",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "COMPANY",
   "row": 1,
   "col": 17,
   "len": 55,
   "initial": "CICS Banking Sample Application - Update Accoun t.",
   "attrb": "NORM,PROT",
   "color": "RED",
   "hilight": ""
  },
  {
   "name": "",
   "row": 3,
   "col": 1,
   "len": 34,
   "initial": "Please provide an ACCOUNT number.",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 5,
   "col": 1,
   "len": 15,
   "initial": "ACCOUNT NUMBER",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "ACCNO",
   "row": 5,
   "col": 17,
   "len": 8,
   "initial": "",
   "attrb": "NORM,NUM,IC,UNPROT",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 5,
   "col": 26,
   "len": 1,
   "initial": "",
   "attrb": "PROT,ASKIP",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 1,
   "len": 18,
   "initial": " Customer Number:",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "CUSTNO",
   "row": 7,
   "col": 20,
   "len": 10,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 7,
   "col": 32,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 1,
   "len": 18,
   "initial": " Sort Code      :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "SORTC",
   "row": 8,
   "col": 20,
   "len": 6,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 8,
   "col": 27,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 1,
   "len": 18,
   "initial": " Account Number :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACCNO2",
   "row": 9,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "PROT,NORM,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 9,
   "col": 32,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 10,
   "col": 1,
   "len": 18,
   "initial": " Account Type   :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACTYPE",
   "row": 10,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 10,
   "col": 29,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 11,
   "col": 1,
   "len": 18,
   "initial": " Interest Rate  :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "INTRT",
   "row": 11,
   "col": 20,
   "len": 7,
   "initial": "",
   "attrb": "UNPROT,FSET,NORM",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 11,
   "col": 28,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 1,
   "len": 18,
   "initial": " Account Opened :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OPENDD",
   "row": 12,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "OPENMM",
   "row": 12,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 12,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "OPENYY",
   "row": 12,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 13,
   "col": 1,
   "len": 18,
   "initial": " Overdraft limit:",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "OVERDR",
   "row": 13,
   "col": 20,
   "len": 8,
   "initial": "",
   "attrb": "NORM,UNPROT,FSET",
   "color": "GREEN",
   "hilight": "UNDERLINE"
  },
  {
   "name": "",
   "row": 13,
   "col": 29,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 1,
   "len": 18,
   "initial": " Last statement :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "LSTMTDD",
   "row": 14,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "LSTMTMM",
   "row": 14,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "LSTMTYY",
   "row": 14,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 14,
   "col": 35,
   "len": 1,
   "initial": "",
   "attrb": "ASKIP,PROT",
   "color": "GREEN",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 1,
   "len": 18,
   "initial": " Next statement :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTDD",
   "row": 15,
   "col": 20,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 23,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTMM",
   "row": 15,
   "col": 25,
   "len": 2,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 15,
   "col": 28,
   "len": 1,
   "initial": "/",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "NSTMTYY",
   "row": 15,
   "col": 30,
   "len": 4,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 16,
   "col": 1,
   "len": 17,
   "initial": " Available Bal  :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "AVBAL",
   "row": 16,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "",
   "row": 17,
   "col": 1,
   "len": 17,
   "initial": " Actual Balance :",
   "attrb": "NORM,PROT",
   "color": "NEUTRAL",
   "hilight": ""
  },
  {
   "name": "ACTBAL",
   "row": 17,
   "col": 19,
   "len": 14,
   "initial": "",
   "attrb": "NORM,PROT,FSET",
   "color": "TURQUOISE",
   "hilight": ""
  },
  {
   "name": "MESSAGE",
   "row": 23,
   "col": 1,
   "len": 79,
   "initial": "",
   "attrb": "BRT,PROT",
   "color": "YELLOW",
   "hilight": ""
  },
  {
   "name": "",
   "row": 24,
   "col": 1,
   "len": 20,
   "initial": "F3=Exit   F12=Cancel",
   "attrb": "NORM,PROT",
   "color": "BLUE",
   "hilight": ""
  },
  {
   "name": "DUMMY",
   "row": 24,
   "col": 79,
   "len": 1,
   "initial": " ",
   "attrb": "DRK,PROT,FSET",
   "color": "GREEN",
   "hilight": ""
  }
 ]
};