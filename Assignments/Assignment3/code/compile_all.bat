if not exist classes mkdir classes
javac -cp .;pdfbox -d classes ir/Engine.java ir/Tokenizer.java ir/TokenTest.java ir/Index.java ir/Indexer.java ir/Searcher.java ir/HashedIndex.java ir/PersistentHashedIndex.java ir/Query.java ir/QueryType.java ir/RankingType.java ir/PostingsList.java ir/PostingsEntry.java ir/SearchGUI.java
