package com.emnify.kvcluster.client.random;

import java.util.Arrays;

/**
 * @author Danilo Oliveira
 */
public class RandomStrings {

    public static final String[] KEYS = ("George Washington\n"
        + "John Adams\n"
        + "Thomas Jefferson\n"
        + "James Madison\n"
        + "James Monroe\n"
        + "John Quincy Adams\n"
        + "Andrew Jackson\n"
        + "Martin Van Buren\n"
        + "William H. Harrison\n"
        + "John Tyler\n"
        + "James K. Polk\n"
        + "Zachary Taylor\n"
        + "Millard Fillmore\n"
        + "Franklin Pierce\n"
        + "James Buchanan\n"
        + "Abraham Lincoln\n"
        + "Andrew Johnson\n"
        + "Ulysses S. Grant\n"
        + "Rutherford B. Hayes\n"
        + "James A. Garfield\n"
        + "Chester A. Arthur\n"
        + "Grover Cleveland\n"
        + "Benjamin Harrison\n"
        + "Grover Cleveland\n"
        + "William McKinley\n"
        + "Theodore Roosevelt\n"
        + "William H. Taft\n"
        + "Woodrow Wilson\n"
        + "Warren G. Harding\n"
        + "Calvin Coolidge\n"
        + "Herbert Hoover\n"
        + "Franklin D. Roosevelt\n"
        + "Harry S. Truman\n"
        + "Dwight D. Eisenhower\n"
        + "John F. Kennedy\n"
        + "Lyndon B. Johnson\n"
        + "Richard M. Nixon\n"
        + "Gerald R. Ford\n"
        + "Jimmy Carter\n"
        + "Ronald Reagan\n"
        + "George H. W. Bush\n"
        + "Bill Clinton\n"
        + "George W. Bush\n"
        + "Barack Hussein Obama\n"
        + "Donald J. Trump").replace(" ", "_").split("\n");

    public static final String[] VALUES = ("FRom faireſt creatures we defire increaſe, \n"
        + "That thereby beauties Roſe might neuer die, \n"
        + "But as the riper ſhould by time deceaſe, \n"
        + "His tender heire might beare his memory: \n"
        + "But thou contracted to thine owne bright eyes, \n"
        + "Feed'ſt thy lights flame with ſelfe ſubſtantiall fewell, \n"
        + "Making a famine where aboundance lies, \n"
        + "Thy ſelfe thy foe,to thy ſweet ſelfe too cruell: \n"
        + "Thou that art now the worlds freſh ornament, \n"
        + "And only herauld to the gaudy ſpring, \n"
        + "Within thine owne bud burieſt thy content, \n"
        + "And tender chorle makſt waſt in niggarding: \n"
        + "   Pitty the world,or elſe this glutton be, \n"
        + "   To eate the worlds due,by the graue and thee."
        + "PERDITA Sir, the year growing ancient, \n"
        + "Not yet on summer's death, nor on the birth \n"
        + "Of trembling winter, the fairest flowers o' the season \n"
        + "Are our carnations and streak'd gillyvors, \n"
        + "Which some call nature's bastards: of that kind \n"
        + "Our rustic garden's barren; and I care not \n"
        + "To get slips of them. \n"
        + "POLIXENES Wherefore, gentle maiden, \n"
        + "Do you neglect them? \n"
        + "PERDITA For I have heard it said \n"
        + "There is an art which in their piedness shares \n"
        + "With great creating nature. \n"
        + "POLIXENES Say there be; \n"
        + "Yet nature is made better by no mean \n"
        + "But nature makes that mean: so, over that art \n"
        + "Which you say adds to nature, is an art \n"
        + "That nature makes. You see, sweet maid, we marry \n"
        + "A gentler scion to the wildest stock, \n"
        + "And make conceive a bark of baser kind \n"
        + "By bud of nobler race: this is an art \n"
        + "Which does mend nature, change it rather, but \n"
        + "The art itself is nature. \n"
        + "PERDITA So it is. \n"
        + "POLIXENES Then make your garden rich in gillyvors, And do not call them bastards. \n"
        + "PERDITA I'll not put \n"
        + "The dibble in earth to set one slip of them; \n"
        + "No more than were I painted I would wish \n"
        + "This youth should say 'twere well and only therefore \n"
        + "Desire to breed by me."
        + "Is it for fear to wet a widow's eye,\n"
        + "That thou consum'st thy self in single life?\n"
        + "Ah! if thou issueless shalt hap to die,\n"
        + "The world will wail thee like a makeless wife;\n"
        + "The world will be thy widow and still weep\n"
        + "That thou no form of thee hast left behind,\n"
        + "When every private widow well may keep\n"
        + "By children's eyes, her husband's shape in mind:\n"
        + "Look what an unthrift in the world doth spend\n"
        + "Shifts but his place, for still the world enjoys it;\n"
        + "But beauty's waste hath in the world an end,\n"
        + "And kept unused the user so destroys it.\n"
        + "   No love toward others in that bosom sits\n"
        + "   That on himself such murd'rous shame commits.").split("\n");

    public static void main(String[] args) {
        String str = Arrays.toString(KEYS);
        System.out.println(str);
    }
}
