package WordNet;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import Main.MarvinSemAnnotator;
import Main.WordMeaningOutputElement;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;

public class Wordnet {
	private Dictionary dict = null;

	// http://wordnetweb.princeton.edu/perl/webwn?s=world

	public Wordnet() {
		String propsFile = "file_properties.xml";
		try {
			JWNL.initialize(new FileInputStream(propsFile));
			dict = Dictionary.getInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public LinkedList<WordMeaningOutputElement> getSencesFromWordnet(
			String word, String tag, int start, int end, String[] tokens,int tokenPos) {

		LinkedList<WordMeaningOutputElement> sences = new LinkedList<WordMeaningOutputElement>();
		try {
			IndexWord Iword = null;
			if (tag.equals("NN") || tag.equals("NNP") || tag.equals("NNS")
					|| tag.equals("NNPS"))
				Iword = dict.lookupIndexWord(POS.NOUN, word);
			else if (tag.equals("VB") || tag.equals("MD") || tag.equals("VBZ")
					|| tag.equals("VBD") || tag.equals("VBG")
					|| tag.equals("VBN") || tag.equals("VBP")
					|| tag.equals("VBZ"))
				Iword = dict.lookupIndexWord(POS.VERB, word);
			else if (tag.equals("JJ") || tag.equals("JJR") || tag.equals("JJS"))
				Iword = dict.lookupIndexWord(POS.ADJECTIVE, word);
			else if (tag.equals("RB") || tag.equals("RBR") || tag.equals("RBS"))
				Iword = dict.lookupIndexWord(POS.ADVERB, word);
			if (Iword == null)
				return sences;
			
			Synset[] senses = WordSenseDisambiguator.getRightSenses(Iword.getSenses(),tokens,tokenPos);
			for (int i = 0; i < senses.length; i++) {
				WordMeaningOutputElement o = new WordMeaningOutputElement();
				o.Description = senses[i].getGloss();
				o.appearingWord = word;
				o.AnnotatorSystem = "WordNet";
				o.id = senses[i].getKey() + "";
				o.startAt = start;
				o.endAt = end;
				o.URL = "http://wordnetweb.princeton.edu/perl/webwn?s="
						+ senses[i].getWord(0).getLemma();
				o.AgentName = MarvinSemAnnotator.WordNetName;
				o.AgentVersion = MarvinSemAnnotator.WordNetVersion;
				o.Location = MarvinSemAnnotator.Location;
				o.EnvironmentDesc = MarvinSemAnnotator.Environment;
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = new Date();
				o.dateTime = dateFormat.format(date);
				sences.add(o);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sences;
	}

}
