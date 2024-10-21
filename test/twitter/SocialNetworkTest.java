package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class SocialNetworkTest {

    private static final Instant time1 = Instant.parse("2024-10-21T10:00:00Z");
    private static final Instant time2 = Instant.parse("2024-10-21T11:00:00Z");

    // Helper method to create tweets
    private Tweet createTweet(long id, String author, String text, Instant timestamp) {
        return new Tweet(id, author, text, timestamp);
    }

    @Test
    public void testGuessFollowsGraph_EmptyList() {
        List<Tweet> tweets = new ArrayList<>();
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraph_TweetsWithoutMentions() {
        List<Tweet> tweets = Arrays.asList(
            createTweet(1, "zaid", "Just a normal tweet", time1),
            createTweet(2, "abdullah", "Another tweet without mentions", time2)
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertTrue(followsGraph.isEmpty());
    }

    @Test
    public void testGuessFollowsGraph_SingleMention() {
        List<Tweet> tweets = Arrays.asList(
            createTweet(1, "zaid", "@abdullah Hi Abdullah!", time1)
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("zaid"));
        assertTrue(followsGraph.get("zaid").contains("abdullah"));
    }

    @Test 
    public void testGuessFollowsGraph_MultipleMentions() {
        List<Tweet> tweets = Arrays.asList(
            createTweet(1, "zaid", "@abdullah @ahmed Let's meet!", time1)
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("zaid"));
        assertTrue(followsGraph.get("zaid").contains("abdullah"));
        assertTrue(followsGraph.get("zaid").contains("ahmed"));
    }

    @Test
    public void testGuessFollowsGraph_MultipleTweetsFromOneUser() {
        List<Tweet> tweets = Arrays.asList(
            createTweet(1, "zaid", "@abdullah Hi again", time1),
            createTweet(2, "zaid", "@ahmed How are you?", time2)
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.containsKey("zaid"));
        assertTrue(followsGraph.get("zaid").contains("abdullah"));
        assertTrue(followsGraph.get("zaid").contains("ahmed"));
    }

    @Test
    public void testInfluencers_EmptyGraph() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue(influencers.isEmpty());
    }

    @Test
    public void testInfluencers_SingleUserWithoutFollowers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("zaid", new HashSet<>());  // No followers
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue(influencers.isEmpty());
    }

    @Test
    public void testInfluencers_SingleInfluencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("zaid", new HashSet<>(Arrays.asList("abdullah")));

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals(1, influencers.size());
        assertEquals("abdullah", influencers.get(0));  // Abdullah has 1 follower (zaid)
    }

    @Test
    public void testInfluencers_MultipleInfluencers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("zaid", new HashSet<>(Arrays.asList("abdullah", "ahmed")));
        followsGraph.put("abdullah", new HashSet<>(Arrays.asList("ahmed")));

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals(2, influencers.size());
        assertEquals("ahmed", influencers.get(0));  // Charlie has 2 followers
        assertEquals("abdullah", influencers.get(1));  // Abdullah has 1 follower
    }

    @Test
    public void testInfluencers_TiedInfluence() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("zaid", new HashSet<>(Arrays.asList("abdullah")));
        followsGraph.put("ahmed", new HashSet<>(Arrays.asList("abdullah")));

        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals(1, influencers.size());
        assertEquals("abdullah", influencers.get(0));  // Abdullah has 2 followers
    }
}
