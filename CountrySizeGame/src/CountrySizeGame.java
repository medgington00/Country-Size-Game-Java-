import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 * 
 * @author MICHAEL EDGINGTON
 *
 */

public class CountrySizeGame implements ActionListener{

	private static ArrayList<Country> list;
	private static DecimalFormat round = new DecimalFormat("0.00");
	private static File highscoreF;
	
	private static JButton leftButton, rightButton;
	private static JLabel labelTop, labelBot, labelMid;
	private static JFrame frame;
	
	private static int streak;
	private static int highscore = 0;
	private static int gamePhase = 0;
	private static int range = 100;
	private static Country[] arr;
	private static String countryString;
	
	public CountrySizeGame() {
		
		frame = new JFrame("Country Size Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(getClass().getResource("/Images/icon.png")).getImage());
		frame.setResizable(false);
		
		addComponentsToPane(frame.getContentPane());
		
		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		
		leftButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	leftButton.setBackground(Color.LIGHT_GRAY);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	leftButton.setBackground(Color.WHITE);
		    }
		}
		);
		rightButton.addMouseListener(new java.awt.event.MouseAdapter() {
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
		    	rightButton.setBackground(Color.LIGHT_GRAY);
		    }

		    public void mouseExited(java.awt.event.MouseEvent evt) {
		    	rightButton.setBackground(Color.WHITE);
		    }
		}
		);
		
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private static void addComponentsToPane(Container pane) {
		
		leftButton = new JButton("");
		leftButton.setPreferredSize(new Dimension(200, 50));
		leftButton.setBackground(Color.WHITE);
		leftButton.setFocusable(false);
		pane.add(leftButton, BorderLayout.LINE_START);
		
		rightButton = new JButton("");
		rightButton.setPreferredSize(new Dimension(200, 50));
		rightButton.setBackground(Color.WHITE);
		rightButton.setFocusable(false);
		pane.add(rightButton, BorderLayout.LINE_END);
		
		labelTop = new JLabel("Which country is bigger?", JLabel.CENTER);
		labelTop.setPreferredSize(new Dimension(500, 25));
		labelTop.setOpaque(true);
		labelTop.setBackground(Color.WHITE);
		pane.add(labelTop, BorderLayout.PAGE_START);
		
		labelMid = new JLabel("Streak: " + streak, JLabel.CENTER);
		labelMid.setPreferredSize(new Dimension(100, 50));
		labelMid.setOpaque(true);
		labelMid.setBackground(Color.WHITE);
		pane.add(labelMid, BorderLayout.CENTER);
		
		labelBot = new JLabel("", JLabel.CENTER);
		labelBot.setPreferredSize(new Dimension(500, 25));
		labelBot.setOpaque(true);
		labelBot.setBackground(Color.WHITE);
		pane.add(labelBot, BorderLayout.PAGE_END);
	}
	
	public static void main(String[] args) throws IOException {
		list = new ArrayList<Country>();
		setCountryString();
		arr = new Country[2];
		
		new CountrySizeGame();
		
		try {
		      highscoreF = new File("highscore.txt");
		      if (highscoreF.createNewFile()) {
		      } else { }
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		
		highscore = getHighscore();
		
		startGame();
	}
	
	private static void startGame() {
		labelTop.setText("Select a difficulty");
		labelMid.setText("");
		labelBot.setText("");
		leftButton.setText("Easy");
		rightButton.setText("Hard");
	}
	
	private static void newCountries() {
		labelTop.setText("Which country is bigger?");
		labelMid.setText("<html>Streak: " + streak + "<br/>High Score: " + highscore + "</html>");
		arr = getTwoCountries(range);
		leftButton.setText(arr[0].getName());
		rightButton.setText(arr[1].getName());
	}
	
	private static void checkAnswer(int ans) {
		int correct = 0;
		if(arr[1].getSize() > arr[0].getSize()) {
			correct = 1;
		}
		
		if(ans == correct) {
			labelBot.setBackground(Color.GREEN);
			labelBot.setText("Correct! " + compareCountries(arr[0],arr[1]));
			streak++;
			if(streak >= highscore) {
				highscore = streak;
			}
			labelMid.setText("<html>Streak: " + streak + "<br/>High Score: " + highscore + "</html>");
			newCountries();
		} else {
			labelBot.setBackground(Color.RED);
			gamePhase = 2;
			leftButton.setText("No");
			rightButton.setText("Yes");
			labelBot.setText("Incorrect. " + compareCountries(arr[0],arr[1]));
			labelTop.setText("Play again?");
		}
	}
	
	private static void addCountriesToArrayList(String[] countryList) throws FileNotFoundException {
		
		for(int i = 0; i < countryList.length; i+=3) {
			Country temp = new Country();
			temp.setName(countryList[i]);
			temp.setSize(Float.parseFloat(countryList[i+1]));
			temp.setRank(Integer.parseInt(countryList[i+2]));
			list.add(temp);
		}
	}
	
	private static String compareCountries(Country c1, Country c2) {
		
		String str = "";
		
		if(c1.getSize() > c2.getSize()) {
			float difference = getDifference(c2,c1);
			str = (c2.getName() + " is " + round.format(difference) + "% the size of " + c1.getName() + ".");
		} else {
			if(c2.getSize() > c1.getSize()) {
				float difference = getDifference(c1,c2);
				str = (c1.getName() + " is " + round.format(difference) + "% the size of " + c2.getName() + ".");
			} else {
				str = (c1.getName() + " and " + c2.getName() + " are the same size! (Within a mile squared)");
			}
		}
		
		return str;
	}
	
	private static float getDifference(Country bigger, Country smaller) {
		float temp = bigger.getSize()/smaller.getSize()*100;
		return temp;
	}
	
	private static float getDifference(float bigger, float smaller) {
		float temp = bigger/smaller*100;
		return temp;
	}
	
	private static int getCountryIndex(String name) {
		int index = -1;
		
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).getName().equals(name)) {
				index = i;
				i = list.size()+1;
			}
		}
		
		return index;
	}
	
	private static Country[] getTwoCountries(int range) {
		Country[] tempList = new Country[2];
		
		Random r = new Random();
		
		if(range < 97) {
			int num1 = r.nextInt(((194-range) - range) + 1) + range;
			int num2 = r.nextInt((range + range) + 1) - range;
			if(num2 == 0)
				num2 = 1;
			
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getRank() == num1) {
					tempList[0] = list.get(i);
					i = list.size()+1;
				}
			}
			
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getRank() == num1 + num2) {
					tempList[1] = list.get(i);
					i = list.size()+1;
				}
			}
		} else {
			int num1 = r.nextInt(195);
			int num2 = r.nextInt(195);
			if(num1==num2) {
				num2++;
			}
			tempList[0] = list.get(num1);
			tempList[1] = list.get(num2);
		}
		
		
		
		return tempList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == leftButton) {
			if(gamePhase == 0) {
				range = 100;
				gamePhase = 1;
				newCountries();
			} else
			if(gamePhase == 1) {
				checkAnswer(0);
			} else
			if(gamePhase == 2) {
				try {
				      FileWriter write = new FileWriter("highscore.txt");
				      write.write(highscore + "");
				      write.close();
				    } catch (IOException e1) {
				      e1.printStackTrace();
				    }
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}	
		}
		if(e.getSource() == rightButton) {
			if(gamePhase == 0) {
				range = 15;
				gamePhase = 1;
				newCountries();
			} else
			if(gamePhase == 1) {
				checkAnswer(1);
			} else
			if(gamePhase == 2) {
				labelBot.setBackground(Color.WHITE);
				gamePhase = 0;
				streak = 0;
				startGame();
			}
		}
	}
	
	private static void setCountryString() throws FileNotFoundException {
		countryString ="Afghanistan\r\n"
				+ "251830\r\n"
				+ "40\r\n"
				+ "Albania\r\n"
				+ "11100\r\n"
				+ "140\r\n"
				+ "Algeria\r\n"
				+ "919595\r\n"
				+ "10\r\n"
				+ "Andorra\r\n"
				+ "181\r\n"
				+ "179\r\n"
				+ "Angola\r\n"
				+ "481400\r\n"
				+ "22\r\n"
				+ "Antigua and Barbuda\r\n"
				+ "171\r\n"
				+ "182\r\n"
				+ "Argentina\r\n"
				+ "1073500\r\n"
				+ "8\r\n"
				+ "Armenia\r\n"
				+ "11484\r\n"
				+ "138\r\n"
				+ "Australia\r\n"
				+ "2969907\r\n"
				+ "6\r\n"
				+ "Austria\r\n"
				+ "32383\r\n"
				+ "113\r\n"
				+ "Azerbaijan\r\n"
				+ "33400\r\n"
				+ "112\r\n"
				+ "Bahamas\r\n"
				+ "5383\r\n"
				+ "155\r\n"
				+ "Bahrain\r\n"
				+ "300\r\n"
				+ "173\r\n"
				+ "Bangladesh\r\n"
				+ "57320\r\n"
				+ "92\r\n"
				+ "Barbados\r\n"
				+ "170\r\n"
				+ "183\r\n"
				+ "Belarus\r\n"
				+ "80200\r\n"
				+ "84\r\n"
				+ "Belgium\r\n"
				+ "11787\r\n"
				+ "136\r\n"
				+ "Belize\r\n"
				+ "8867\r\n"
				+ "147\r\n"
				+ "Benin\r\n"
				+ "44310\r\n"
				+ "100\r\n"
				+ "Bhutan\r\n"
				+ "14824\r\n"
				+ "133\r\n"
				+ "Bolivia\r\n"
				+ "424164\r\n"
				+ "27\r\n"
				+ "Bosnia and Herzegovina\r\n"
				+ "19772\r\n"
				+ "125\r\n"
				+ "Botswana\r\n"
				+ "224610\r\n"
				+ "47\r\n"
				+ "Brazil\r\n"
				+ "3287956\r\n"
				+ "5\r\n"
				+ "Brunei\r\n"
				+ "2226\r\n"
				+ "164\r\n"
				+ "Bulgaria\r\n"
				+ "42858\r\n"
				+ "103\r\n"
				+ "Burkina Faso\r\n"
				+ "105878\r\n"
				+ "74\r\n"
				+ "Burundi\r\n"
				+ "10747\r\n"
				+ "142\r\n"
				+ "Cambodia\r\n"
				+ "69898\r\n"
				+ "88\r\n"
				+ "Cameroon\r\n"
				+ "183569\r\n"
				+ "53\r\n"
				+ "Canada\r\n"
				+ "3855100\r\n"
				+ "2\r\n"
				+ "Cape Verde\r\n"
				+ "1557\r\n"
				+ "166\r\n"
				+ "Central African Republic\r\n"
				+ "240535\r\n"
				+ "44\r\n"
				+ "Chad\r\n"
				+ "496000\r\n"
				+ "20\r\n"
				+ "Chile\r\n"
				+ "291933\r\n"
				+ "37\r\n"
				+ "China\r\n"
				+ "3705407\r\n"
				+ "3\r\n"
				+ "Colombia\r\n"
				+ "440831\r\n"
				+ "25\r\n"
				+ "Comoros\r\n"
				+ "719\r\n"
				+ "170\r\n"
				+ "Republic of the Congo\r\n"
				+ "132000\r\n"
				+ "64\r\n"
				+ "Democratic Republic of the Congo\r\n"
				+ "905355\r\n"
				+ "11\r\n"
				+ "Costa Rica\r\n"
				+ "19700\r\n"
				+ "126\r\n"
				+ "Croatia\r\n"
				+ "21851\r\n"
				+ "124\r\n"
				+ "Cuba\r\n"
				+ "42426\r\n"
				+ "104\r\n"
				+ "Cyprus\r\n"
				+ "3572\r\n"
				+ "162\r\n"
				+ "Czech Republic\r\n"
				+ "30452\r\n"
				+ "115\r\n"
				+ "Denmark\r\n"
				+ "16639\r\n"
				+ "130\r\n"
				+ "Djibouti\r\n"
				+ "9000\r\n"
				+ "146\r\n"
				+ "Dominica\r\n"
				+ "290\r\n"
				+ "174\r\n"
				+ "Dominican Republic\r\n"
				+ "18792\r\n"
				+ "128\r\n"
				+ "East Timor (Timor-Leste)\r\n"
				+ "5760\r\n"
				+ "154\r\n"
				+ "Ecuador\r\n"
				+ "106889\r\n"
				+ "73\r\n"
				+ "Egypt\r\n"
				+ "387050\r\n"
				+ "29\r\n"
				+ "El Salvador\r\n"
				+ "8124\r\n"
				+ "148\r\n"
				+ "Equatorial Guinea\r\n"
				+ "10831\r\n"
				+ "141\r\n"
				+ "Eritrea\r\n"
				+ "45400\r\n"
				+ "99\r\n"
				+ "Estonia\r\n"
				+ "17462\r\n"
				+ "129\r\n"
				+ "Eswatini\r\n"
				+ "6704\r\n"
				+ "153\r\n"
				+ "Ethiopia\r\n"
				+ "426400\r\n"
				+ "26\r\n"
				+ "Fiji\r\n"
				+ "7055\r\n"
				+ "151\r\n"
				+ "Finland\r\n"
				+ "130667\r\n"
				+ "65\r\n"
				+ "France\r\n"
				+ "247368\r\n"
				+ "42\r\n"
				+ "Gabon\r\n"
				+ "103347\r\n"
				+ "76\r\n"
				+ "Gambia\r\n"
				+ "4361\r\n"
				+ "159\r\n"
				+ "Georgia\r\n"
				+ "26900\r\n"
				+ "119\r\n"
				+ "Germany\r\n"
				+ "137882\r\n"
				+ "63\r\n"
				+ "Ghana\r\n"
				+ "92098\r\n"
				+ "80\r\n"
				+ "Greece\r\n"
				+ "50949\r\n"
				+ "95\r\n"
				+ "Grenada\r\n"
				+ "133\r\n"
				+ "185\r\n"
				+ "Guatemala\r\n"
				+ "42042\r\n"
				+ "105\r\n"
				+ "Guinea\r\n"
				+ "94926\r\n"
				+ "77\r\n"
				+ "Guinea-Bissau\r\n"
				+ "13948\r\n"
				+ "134\r\n"
				+ "Guyana\r\n"
				+ "83000\r\n"
				+ "83\r\n"
				+ "Haiti\r\n"
				+ "10710\r\n"
				+ "143\r\n"
				+ "Honduras\r\n"
				+ "43433\r\n"
				+ "101\r\n"
				+ "Hungary\r\n"
				+ "35918\r\n"
				+ "108\r\n"
				+ "Iceland\r\n"
				+ "40000\r\n"
				+ "106\r\n"
				+ "India\r\n"
				+ "1269219\r\n"
				+ "7\r\n"
				+ "Indonesia\r\n"
				+ "737815\r\n"
				+ "14\r\n"
				+ "Iran\r\n"
				+ "636372\r\n"
				+ "17\r\n"
				+ "Iraq\r\n"
				+ "169235\r\n"
				+ "58\r\n"
				+ "Ireland\r\n"
				+ "27133\r\n"
				+ "118\r\n"
				+ "Israel\r\n"
				+ "8020\r\n"
				+ "149\r\n"
				+ "Italy\r\n"
				+ "116348\r\n"
				+ "71\r\n"
				+ "Ivory Coast\r\n"
				+ "124504\r\n"
				+ "68\r\n"
				+ "Jamaica\r\n"
				+ "4244\r\n"
				+ "160\r\n"
				+ "Japan\r\n"
				+ "145937\r\n"
				+ "62\r\n"
				+ "Jordan\r\n"
				+ "34495\r\n"
				+ "110\r\n"
				+ "Kazakhstan\r\n"
				+ "1052100\r\n"
				+ "9\r\n"
				+ "Kenya\r\n"
				+ "224081\r\n"
				+ "48\r\n"
				+ "Kiribati\r\n"
				+ "313\r\n"
				+ "172\r\n"
				+ "North Korea\r\n"
				+ "46540\r\n"
				+ "97\r\n"
				+ "South Korea\r\n"
				+ "38690\r\n"
				+ "107\r\n"
				+ "Kuwait\r\n"
				+ "6880\r\n"
				+ "152\r\n"
				+ "Kyrgyzstan\r\n"
				+ "77202\r\n"
				+ "85\r\n"
				+ "Laos\r\n"
				+ "91400\r\n"
				+ "82\r\n"
				+ "Latvia\r\n"
				+ "24926\r\n"
				+ "122\r\n"
				+ "Lebanon\r\n"
				+ "4036\r\n"
				+ "161\r\n"
				+ "Lesotho\r\n"
				+ "11720\r\n"
				+ "127\r\n"
				+ "Liberia\r\n"
				+ "43000\r\n"
				+ "102\r\n"
				+ "Libya\r\n"
				+ "679360\r\n"
				+ "16\r\n"
				+ "Liechtenstein\r\n"
				+ "62\r\n"
				+ "190\r\n"
				+ "Lithuania\r\n"
				+ "25200\r\n"
				+ "121\r\n"
				+ "Luxembourg\r\n"
				+ "998\r\n"
				+ "168\r\n"
				+ "Madagascar\r\n"
				+ "226658\r\n"
				+ "46\r\n"
				+ "Malawi\r\n"
				+ "45747\r\n"
				+ "98\r\n"
				+ "Malaysia\r\n"
				+ "127724\r\n"
				+ "67\r\n"
				+ "Maldives\r\n"
				+ "120\r\n"
				+ "187\r\n"
				+ "Mali\r\n"
				+ "478841\r\n"
				+ "23\r\n"
				+ "Malta\r\n"
				+ "122\r\n"
				+ "186\r\n"
				+ "Marshall Islands\r\n"
				+ "70\r\n"
				+ "189\r\n"
				+ "Mauritania\r\n"
				+ "398000\r\n"
				+ "28\r\n"
				+ "Mauritius\r\n"
				+ "790\r\n"
				+ "169\r\n"
				+ "Mexico\r\n"
				+ "758449\r\n"
				+ "13\r\n"
				+ "Micronesia\r\n"
				+ "271\r\n"
				+ "177\r\n"
				+ "Moldova\r\n"
				+ "13068\r\n"
				+ "135\r\n"
				+ "Monaco\r\n"
				+ "0.78\r\n"
				+ "194\r\n"
				+ "Mongolia\r\n"
				+ "603910\r\n"
				+ "18\r\n"
				+ "Montenegro\r\n"
				+ "5333\r\n"
				+ "156\r\n"
				+ "Morocco\r\n"
				+ "172410\r\n"
				+ "57\r\n"
				+ "Mozambique\r\n"
				+ "309500\r\n"
				+ "35\r\n"
				+ "Myanmar/Burma\r\n"
				+ "261228\r\n"
				+ "39\r\n"
				+ "Namibia\r\n"
				+ "318772\r\n"
				+ "34\r\n"
				+ "Nauru\r\n"
				+ "8.1\r\n"
				+ "193\r\n"
				+ "Nepal\r\n"
				+ "56827\r\n"
				+ "93\r\n"
				+ "Netherlands\r\n"
				+ "16160\r\n"
				+ "131\r\n"
				+ "New Zealand\r\n"
				+ "104428\r\n"
				+ "75\r\n"
				+ "Nicaragua\r\n"
				+ "50337\r\n"
				+ "96\r\n"
				+ "Niger\r\n"
				+ "489000\r\n"
				+ "21\r\n"
				+ "Nigeria\r\n"
				+ "356669\r\n"
				+ "31\r\n"
				+ "North Macedonia\r\n"
				+ "9928\r\n"
				+ "145\r\n"
				+ "Norway\r\n"
				+ "148729\r\n"
				+ "61\r\n"
				+ "Oman\r\n"
				+ "119500\r\n"
				+ "70\r\n"
				+ "Pakistan\r\n"
				+ "350520\r\n"
				+ "33\r\n"
				+ "Palau\r\n"
				+ "177\r\n"
				+ "180\r\n"
				+ "Palestine\r\n"
				+ "2320\r\n"
				+ "163\r\n"
				+ "Panama\r\n"
				+ "29119\r\n"
				+ "116\r\n"
				+ "Papua New Guinea\r\n"
				+ "178700\r\n"
				+ "54\r\n"
				+ "Paraguay\r\n"
				+ "157048\r\n"
				+ "59\r\n"
				+ "Peru\r\n"
				+ "496225\r\n"
				+ "19\r\n"
				+ "Philippines\r\n"
				+ "120000\r\n"
				+ "72\r\n"
				+ "Poland\r\n"
				+ "120733\r\n"
				+ "69\r\n"
				+ "Portugal\r\n"
				+ "35609\r\n"
				+ "109\r\n"
				+ "Qatar\r\n"
				+ "4473\r\n"
				+ "158\r\n"
				+ "Romania\r\n"
				+ "92046\r\n"
				+ "81\r\n"
				+ "Russia\r\n"
				+ "6601670\r\n"
				+ "1\r\n"
				+ "Rwanda\r\n"
				+ "10169\r\n"
				+ "144\r\n"
				+ "Saint Kitts and Nevis\r\n"
				+ "101\r\n"
				+ "188\r\n"
				+ "Saint Lucia\r\n"
				+ "238\r\n"
				+ "178\r\n"
				+ "Saint Vincent and the Grenadines\r\n"
				+ "150\r\n"
				+ "184\r\n"
				+ "Samoa\r\n"
				+ "1097\r\n"
				+ "167\r\n"
				+ "San Marino\r\n"
				+ "24\r\n"
				+ "191\r\n"
				+ "Sao Tome and Principe\r\n"
				+ "372\r\n"
				+ "171\r\n"
				+ "Saudi Arabia\r\n"
				+ "830000\r\n"
				+ "12\r\n"
				+ "Senegal\r\n"
				+ "75955\r\n"
				+ "86\r\n"
				+ "Serbia\r\n"
				+ "34116\r\n"
				+ "111\r\n"
				+ "Seychelles\r\n"
				+ "175\r\n"
				+ "181\r\n"
				+ "Sierra Leone\r\n"
				+ "27700\r\n"
				+ "117\r\n"
				+ "Singapore\r\n"
				+ "281\r\n"
				+ "176\r\n"
				+ "Slovakia\r\n"
				+ "18933\r\n"
				+ "127\r\n"
				+ "Slovenia\r\n"
				+ "7827\r\n"
				+ "150\r\n"
				+ "Solomon Islands\r\n"
				+ "11157\r\n"
				+ "139\r\n"
				+ "Somalia\r\n"
				+ "246201\r\n"
				+ "43\r\n"
				+ "South Africa\r\n"
				+ "471445\r\n"
				+ "24\r\n"
				+ "South Sudan\r\n"
				+ "248777\r\n"
				+ "41\r\n"
				+ "Spain\r\n"
				+ "195365\r\n"
				+ "51\r\n"
				+ "Sri Lanka\r\n"
				+ "25330\r\n"
				+ "120\r\n"
				+ "Sudan\r\n"
				+ "718723\r\n"
				+ "15\r\n"
				+ "Suriname\r\n"
				+ "63250\r\n"
				+ "90\r\n"
				+ "Sweden\r\n"
				+ "173860\r\n"
				+ "55\r\n"
				+ "Switzerland\r\n"
				+ "15940\r\n"
				+ "132\r\n"
				+ "Syria\r\n"
				+ "71500\r\n"
				+ "87\r\n"
				+ "Tajikistan\r\n"
				+ "55300\r\n"
				+ "94\r\n"
				+ "Tanzania\r\n"
				+ "364900\r\n"
				+ "30\r\n"
				+ "Thailand\r\n"
				+ "198120\r\n"
				+ "50\r\n"
				+ "Togo\r\n"
				+ "21925\r\n"
				+ "123\r\n"
				+ "Tonga\r\n"
				+ "288\r\n"
				+ "175\r\n"
				+ "Trinidad and Tobago\r\n"
				+ "1980\r\n"
				+ "165\r\n"
				+ "Tunisia\r\n"
				+ "63170\r\n"
				+ "91\r\n"
				+ "Turkey\r\n"
				+ "302535\r\n"
				+ "36\r\n"
				+ "Turkmenistan\r\n"
				+ "188500\r\n"
				+ "52\r\n"
				+ "Tuvalu\r\n"
				+ "10\r\n"
				+ "192\r\n"
				+ "Uganda\r\n"
				+ "93260\r\n"
				+ "79\r\n"
				+ "Ukraine\r\n"
				+ "233000\r\n"
				+ "45\r\n"
				+ "United Arab Emirates\r\n"
				+ "32300\r\n"
				+ "114\r\n"
				+ "United Kingdom\r\n"
				+ "93628\r\n"
				+ "78\r\n"
				+ "United States of America\r\n"
				+ "3677649\r\n"
				+ "4\r\n"
				+ "Uruguay\r\n"
				+ "68037\r\n"
				+ "89\r\n"
				+ "Uzbekistan\r\n"
				+ "172700\r\n"
				+ "56\r\n"
				+ "Vanuatu\r\n"
				+ "4706\r\n"
				+ "157\r\n"
				+ "Vatican City\r\n"
				+ "0.19\r\n"
				+ "195\r\n"
				+ "Venezuela\r\n"
				+ "353841\r\n"
				+ "32\r\n"
				+ "Vietnam\r\n"
				+ "127882\r\n"
				+ "66\r\n"
				+ "Yemen\r\n"
				+ "214000\r\n"
				+ "49\r\n"
				+ "Zambia\r\n"
				+ "290585\r\n"
				+ "38\r\n"
				+ "Zimbabwe\r\n"
				+ "150872\r\n"
				+ "60";
		String[] countryArr = countryString.split("\r\n");
		
		addCountriesToArrayList(countryArr);
	}

	private static int getHighscore() throws FileNotFoundException {
		
		Scanner scan = new Scanner(highscoreF);
		int i = 0;
		
		if(scan.hasNextInt()) { 
			i = scan.nextInt();
		}
		
		scan.close();
		
		return i;
	}
}