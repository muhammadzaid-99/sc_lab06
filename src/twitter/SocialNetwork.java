package twitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * SocialNetwork provides methods that operate on a social network.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     *
     * @param tweets a list of tweets providing the evidence, not modified by this method.
     * @return a social network (as defined above) in which Ernie follows Bert if and only if
     *         there is evidence for it in the given list of tweets.
     */
	public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
	    Map<String, Set<String>> followsGraph = new HashMap<>();

	    for (Tweet tweet : tweets) {
	        String author = tweet.getAuthor().toLowerCase(); // not case sensitive
	        
	        // extract metions from tweets
	        List<String> mentions = extractMentions(tweet.getText());

	        // only add author if have mentions
	        if (!mentions.isEmpty()) {
	            followsGraph.putIfAbsent(author, new HashSet<>());
	            for (String mention : mentions) {
	                String normalizedMention = mention.toLowerCase();
	                followsGraph.putIfAbsent(normalizedMention, new HashSet<>());
	                followsGraph.get(author).add(normalizedMention);
	            }
	        }
	    }
 
	    return followsGraph;
	}

 
    /**
     * private method to extract mentions from the tweet text.
     *
     * @param text the text of the tweet
     * @return a list of usernames mentioned in the tweet
     */
    private static List<String> extractMentions(String text) {
        List<String> mentions = new ArrayList<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.startsWith("@")) {
                // remove @ and add the mention to the list
                mentions.add(word.substring(1));
            }
        }
        return mentions;
    } 

    /**
     * Find the people in a social network who have the greatest influence, in the sense that they have the most followers.
     *
     * @param followsGraph a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followerCount = new HashMap<>();

        // count followers for each user
        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            String user = entry.getKey();
            for (String followed : entry.getValue()) {
                followerCount.put(followed, followerCount.getOrDefault(followed, 0) + 1);
            }
        }
 
        // create a sorted list of influencers based on follower count
        List<String> influencers = new ArrayList<>(followerCount.keySet());
        influencers.sort((u1, u2) -> {
            int compare = Integer.compare(followerCount.get(u2), followerCount.get(u1)); // descending order
            if (compare == 0) {
                return u1.compareTo(u2); // alphabetical order for ties
            }
            return compare;
        });
 
        return influencers;
    }
}