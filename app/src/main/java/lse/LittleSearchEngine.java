package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String, Occurrence> hm=new HashMap<String, Occurrence>();
		Scanner sc=new Scanner(new File(docFile));
		while(sc.hasNext()){
			String newWord=getKeyword(sc.next());
			if(newWord!=null){
				if(hm.get(newWord)!=null){
					hm.get(newWord).frequency++;
				}
				else{
					Occurrence nw=new Occurrence(docFile, 1);
					hm.put(newWord, nw);
				}
			}
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return hm;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		for(String key : kws.keySet()){
			if(keywordsIndex.get(key)!=null){
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			}
			else{
				ArrayList<Occurrence> occ=new ArrayList<Occurrence>();
				occ.add(kws.get(key));
				keywordsIndex.put(key, occ);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		HashSet<String> pun=new HashSet<String>();
		pun.add("."); pun.add(","); pun.add("?"); pun.add(":"); pun.add(";"); pun.add("!");
		//Punctuation Strip
		while(!pun.contains(word) && pun.contains(word.substring(word.length()-1))){
			word=word.substring(0, word.length()-1);
		}
		//Punctuation Check
		for(int i=0; i<word.length(); i++){
			if(pun.contains(word.substring(i, i+1))) return null;
		}
		//Set to Lowercase
		word=word.toLowerCase();
		//Noise Word Check
		if(noiseWords.contains(word)) return null;
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		ArrayList<Integer> in=new ArrayList<Integer>();
		int low=0; int high=occs.size()-2; int mid=0; int c=occs.size()-1;
		while(low<=high){
			mid=(low+high)/2;
			in.add(mid);
			if(occs.get(mid).frequency<occs.get(c).frequency){
				high=mid-1;
			}
			else if(occs.get(mid).frequency>occs.get(c).frequency){
				low=mid+1;
			}
			else{
				break;
			}
		}
		occs.add(mid+1, occs.get(c));
		occs.remove(c+1);
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		if(in.size()>1) return in;
		return null;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		if (keywordsIndex.get(kw1)==null && keywordsIndex.get(kw2)==null) return null;
		ArrayList<String> list=new ArrayList<String>();
		ArrayList<Occurrence> k1=keywordsIndex.get(kw1); ArrayList<Occurrence> k2=keywordsIndex.get(kw2);
		HashMap<String, Integer> k3=new HashMap <String, Integer>();
		for(Occurrence k : k1){
			k3.put(k.document, k.frequency);
		}
		for(Occurrence k : k2){
			if(k3.get(k.document)!=null){
				int i=k3.get(k.document)+k.frequency;
				k3.replace(k.document, i);
			}
		}
		while(k3.size()>0){
			String most="";
			for(String key : k3.keySet()){
				if(most.equals("") || k3.get(key)>k3.get(most)) most=key;
			}
			list.add(most);
			k3.remove(most);
		}
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return list;
	}
}
