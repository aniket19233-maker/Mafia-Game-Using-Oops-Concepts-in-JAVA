import java.util.*;
import java.io.*;
import java.lang.*;


//abstract class Player
abstract class Player
{	
	private int ID;           //ID attribute
	public int hp;			 //hp attribute
	
	public void setID(int ID) {this.ID=ID;}							//setter method to setID
	
	public int getID(){return this.ID;}								//getter method to get ID
	
	public void setHp(int hp) { this.hp = hp;}						//setter method to set Hp
	
	public int getHp() {return this.hp;}							//getter method to get Hp
	
	public boolean isDead() {return this.hp<=0;}					//isDead method to see if its dead or not
	
	public void decreaseHp(int h) {this.hp-=h;}						//decrease hp
	
	public void increaseHp(int h) {this.hp+=h;}						//increase hp
	
	public abstract String getName();								//getName abstract function
	
}

//A Commoner class extending Player
class Commoner extends Player
{
	//Commoner class Constructor
	public Commoner() { this.setHp(1000);}
	//get Name function
	public String getName() { return "Commoner"; }
}

//A Healer class extending Player
class Healer extends Player
{
	//Healer class Constructor
	public Healer() { this.setHp(800); }
	//get Name function
	public String getName() { return "Healer";}
}

//A Detective class extending Player
class Detective extends Player
{
	//Detective class Constructor
	public Detective(){ this.setHp(800); }
	//get Name function
	public String getName() { return "Detective";}
}

//A Mafia class extending Player
class Mafia extends Player
{
	//Mafia class Constructor
	public Mafia(){ this.setHp(2500); }
	//get Name function
	public String getName() { return "Mafia"; }
	@Override
	public boolean isDead() { return false; }	// overriding isDead function 
}

//A Generic User class extending Player
class User<T> extends Player
{
	T t;			// Generic class instance
	
	//User Class Constructor
	public User(T t){
		this.t = t;				// assign the new Generic object with the Generic object reference passed in the constructor
		
		//setting Hp of the User
		if(this.getName().equals("Mafia")) this.setHp(2500);     
		else if(this.getName().equals("Detective")||this.getName().equals("Healer")) this.setHp(800);
		else this.setHp(1000);
	}
	
	//getName function of the User
	public String getName() {
		String s= t.getClass().toString(),res="";
		for(int i=6;i<s.length();i++) res+=s.charAt(i);
		return res;
	}
	
	@Override
	public boolean isDead() {
		if(this.getName().equals("Mafia")) {
			return false;
		}
		return this.hp<=0;
	}
}

class SortByVotes implements Comparator<Object>{
	public int compare(Object a,Object b) {
		if((a instanceof Integer) && (b instanceof Integer))
			return (int)a-(int)b;
		else
			return 0;
	}
}


//class RunApp used to run the main functionalities of the App
class RunApp
{
	//Random class Object 
	Random rand = new Random();
	//Scanner Object to take input
	Scanner sc = new Scanner(System.in);
	
	//Two lists i.e. one of the main list of the players
	private ArrayList<Player> list;
	
	//this list is to handle the information of every around
	private ArrayList<Object> roundStatus;
	
	//round attribute
	private int round;
	
	//user attribute
	private int user;
	
	//mafiaCount attribute
	private int mafiaCount;
	
	//detectiveCount attribute
	private int detectiveCount;
	
	//healerCount attribute
	private int healerCount;
	
	//commoner count attribute
	private int commonerCount;
	
	//total attribute
	private int total;
	
	//vote attribute to see if voting will happen or not
	private boolean vote;
	
	//class RunApp constructor with total players passed as fields
	public RunApp(int pl)
	{
		//initializing attributes
		this.mafiaCount=this.detectiveCount=this.healerCount=this.commonerCount=0;
		this.roundStatus = new ArrayList<Object>();
		this.vote = true;
		this.total=pl;
		this.round=1;
		this.setPlayers(pl);
		this.run(pl);
	}
	
	//method that runs the game by passing the total number of players
	private void run(int pl)
	{
		//select an  option to choose the role of the user
		int opt =sc.nextInt();
		
		//enter the user with option selected and total players
		enterUser(opt,pl);
		
		//errors handled if the input passed is not valid
		while(opt<1 || opt>5) enterUser(opt=sc.nextInt(),pl);
		
		//play the rounds
		this.PlayRounds();
	}
	
	//play rounds method 
	private void PlayRounds() {
		
		//run the loop until the game is over
		while(!isGameOver()) {
			
			//if the Mafia is not voted out
			if(this.mafiaCount>0) {
			System.out.println("Round "+this.round+":");
			
			System.out.print(this.list.size()+" players are remaining: ");
			
			//print out the players remaining in the game with their ID's 
			for(Player p:this.list) System.out.print("Player"+(p.getID())+" ");

			System.out.print("are alive.\n");
			
			//implement the MafiaRole
			this.MafiaRole();
			
			//implement the Detective's role
			int v = this.DetectiveRole();

			System.out.println("Healers have chosen someone to heal");
			
			//implement the Healer Role
			if(this.healerCount>0)
				this.HealerRole();
			
			System.out.println("--End of actions--");
			
			//check for the Dead players
			this.checkDeadPlayers();
			
			//if Vote is to be done call the voting process method
			if(vote) {
				this.votingProcess();
			}
			//else we have to vote a Mafia as chosen by the detectives
			else {
				if(v!=-1) {
					this.voteOutMafia(v);
				}
			}
			
			//reset all attributes
			this.resetAttributes();
			
			System.out.println("--End of Round"+(this.round++)+"--\n\n\n");
			
			//if Mafia not present then stop the loop
		}
		else {break;}
		}
		//Game is over now print out the whole game status
		System.out.println("\n\nGame Over.");
		
		this.gameResult();
	}
	
	//Vote out Mafia
	private void voteOutMafia(int p) {
		//decrease the Mafia count
		this.mafiaCount--; 
		//add to the round status
		this.roundStatus.add("Player"+this.list.get(p).getID()+" is voted out");
		//remove the voted person from the list
		this.list.remove(p);
	}
	
	//reset attributes before starting up a new round
	private void resetAttributes() {
		this.vote=true;
		for(Object o:this.roundStatus) System.out.println(o);
		this.roundStatus.clear();
	}
	
	//game result whether Mafia have won or not
	private void gameResult() {
		
		//check if Mafia has won
		if(this.mafiaCount>0&&this.mafiaCount == this.list.size()-this.mafiaCount) 
			System.out.println("Mafias have won.");
		
		//check if Mafia have lost
		else System.out.println("Mafias have lost");
		
		//print the status of Mafia
		for(int i=0;i<calcMafias(total);i++)
			System.out.print("Player" +(i+1)+" ");
		System.out.println("were mafias");
		
		//print the status of Detectives
		for(int i=calcMafias(total);i<calcMafias(total)+calcDetectives(total);i++)
			System.out.print("Player" +(i+1)+" ");
		System.out.println("were detectives");
		
		//print the status of Healers
		for(int i=calcMafias(total)+calcDetectives(total);i<calcMafias(total)+calcDetectives(total)+calcHealers(total);i++)
			System.out.print("Player" +(i+1)+" ");
		System.out.println("were healers");
		
		//print the status of Commoners
		for(int i=calcMafias(total)+calcDetectives(total)+calcHealers(total);i<total;i++)
			System.out.print("Player" +(i+1)+" ");
		System.out.println("were commoners");
	}
	
	//implement check Dead Players
	private void checkDeadPlayers() {
		int ct=0;       //count to see if no one is dead
		
		//iterate over whole array
		for(int i=0;i<this.list.size();i++) {
			
			//check if the person is dead and not a Mafia
			if(this.list.get(i).isDead()&& !this.isMafia(i)) {
				ct++;		//increase the count
				
				//add to round status
				System.out.println("Player"+this.list.get(i).getID()+" has died.");
				
				//check if the user is dead or not
				if(this.list.get(i).getID()==this.user) this.user=-1;
				
				//remove it from the list
				this.list.remove(i);
			}
		}
		
		//check if died or not
		if(ct==0) System.out.println("No one died.");
	}
	
	//implements the Healer Role
	private void HealerRole() {
		
		// find the user index
		int j=this.findID(this.user);
		
		//check if user is -1 or if user is not present in the list or is not a healer
		if(this.user==-1||(j==this.list.size()||!this.list.get(j).getName().equals("Healer"))) {
			
			// randomly choose a integer
			int p = rand.nextInt(this.list.size());
			
			//increase Hp of the person
			this.list.get(p).increaseHp(this.healerCount*500);
		}
		
		//choosing a player to heal
		else {
			System.out.println("Choose a Player to Heal: ");
			
			//find ID of the chosen person
			int chosen =sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(chosen)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				chosen = sc.nextInt();
			}
			
			int p=this.findID(chosen);
			
			//increase the Hp of the person
			this.list.get(p).increaseHp(this.healerCount*500);
		}
	}
	
	//implement the Detective Role
	private int DetectiveRole() {
		//find the id of the user
		int j=this.findID(this.user);
		
		//if the user ID is -1 or its not found in the list or it's a detective
		if(this.user==-1||(j>=this.list.size()||!this.list.get(j).getName().equals("Detective"))) {
			
			if(this.detectiveCount>0)
				System.out.println("Detectives have chosen someone to test");
			//choose a person randomly
			int p = rand.nextInt(this.list.size());
			
			//choose until the person chosen is not a detective
			while(this.list.get(p).getName().equals("Detective")) {
				p = rand.nextInt(this.list.size());					//choose randomly
			}
			
			// see if its a Mafia or not
			if(this.list.get(p).getName().equals("Mafia")) {
				this.vote=false;
				return p;
			}
			
			
			return -1; // if not then return -1.
		}
		
		//else it's a user 
		else {
			System.out.print("Choose a Player to test: ");
			
			int chosen =sc.nextInt();
			//check if given user exists or not 
			while(!this.Exists(chosen)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				chosen = sc.nextInt();
			}
			
			//ask for a person to test
			int p = this.findID(chosen);
			
			//error handling if the person chosen is a detective chose again
			while(this.list.get(p).getName().equals("Detective")) {
				System.out.println("a Detective can't select a Detective. Choose a Player to test: ");
				p = this.findID(sc.nextInt());
			}
			
			//if the chosen person is not a Mafia we can carry out the process
			if(this.list.get(p).getName().equals("Mafia")) {
				System.out.println("Player"+this.list.get(p).getID()+" has been chosen and is a Mafia");
				this.vote=false;
				return p;
			}
			System.out.println("Player"+this.list.get(p).getID()+" been chosen and is not a Mafia");			
			
			//return -1 if not a Mafia	
			return -1;
		}
	}
	
	//Mafia Role implementation 
	private void MafiaRole() {
		
		//find the user index
		int j=this.findID(this.user);
		
		//if the user ID is -1 or its not found in the list or it's a detective
		if(this.user==-1||(j==this.list.size()||!this.list.get(j).getName().equals("Mafia"))) {
			System.out.println("\nMafias have Chosen thier target");
			this.mafiasChooseTarget();			//choose a target 
		}
		
		//else its a user
		else {
			System.out.println("Choose a target: ");
			
			int chosen=sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(chosen)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				chosen = sc.nextInt();
			}
			
			//find a person index in list according to its ID
			int c = this.findID(chosen);
			
			//check if it's not a Mafia else choose again
			while(this.list.get(c).getName().equals("Mafia"))
			{
				System.out.println("Cannot vote a mafia. Choose a target again: ");
				c = this.findID(sc.nextInt());
			}
			
			//decrease the Hp of the chosen target
			System.out.println("\nMafias have Chosen thier target");
			this.list.get(c).decreaseHp(this.giveMafiaHp());
		}
	}
	
	//check if the given player is there in the list
	private boolean Exists(int c) {
		for(Player p:this.list)	if(p.getID()==c) return true;
		return false;
	}
	
	//function that enters the user
	private void enterUser(int opt,int pl) {	
		
		//if option chosen is 1
		if(opt==1)
		{
			
			//create a Mafia type Object for the User  
			User<Mafia> u = new User<Mafia>(new Mafia());
			//setID as 1
			u.setID(1);
			//replace the object of player by the user Object
			list.set(0,u);
			
			//set user with ID
			this.user=1;
			System.out.print("You are Player"+u.getID()+"\nYou are a mafia. Other mafias are:[ ");
			
			//tell user which Player you are
			for(int i=1;i<calcMafias(pl);i++)
				System.out.print("Player"+this.list.get(i).getID()+" ");
			System.out.println("]");
		}
		
		//if option chosen is 2
		else if(opt == 2)
		{
			//create a Detective type Object for the User 
			User<Detective> u = new User<Detective>(new Detective());
			
			//setID as calcMafias(pl)+1
			u.setID(calcMafias(pl)+1);
			
			//replace the object of player by the user Object
			this.list.set(calcMafias(pl), u);

			//set user with ID
			this.user=calcMafias(pl)+1;
			System.out.print("You are Player"+u.getID()+"\nYou are a detective. Other detectives are:[");
			
			//tell user which Player you are
			for(int i=calcMafias(pl)+1;i<calcMafias(pl)+calcDetectives(pl);i++)
				System.out.print("Player"+this.list.get(i).getID()+" ");
			System.out.println("]");
		}
		
		//if option chosen is 3
		else if(opt == 3)
		{
			//create a Healer type Object for the User 
			User<Healer> u = new User<Healer>(new Healer());
			
			//setID as calcMafias(pl)+calcDetectives(pl)+1
			u.setID(calcMafias(pl)+calcDetectives(pl)+1);
			
			//replace the object of player by the user Object
			this.list.set(calcMafias(pl)+calcDetectives(pl), u);

			//set user with ID
			this.user = calcMafias(pl)+calcDetectives(pl)+1;
			System.out.print("You are Player"+u.getID()+"\nYou are a Healer. Other healers are:[");
			
			//tell user which Player you are
			for(int i=calcMafias(pl)+calcDetectives(pl)+1;i<calcMafias(pl)+calcDetectives(pl)+calcHealers(pl);i++)
				System.out.print("Player"+this.list.get(i).getID()+" ");
			System.out.println("]");
		}
		
		//if option chosen is 4
		else if(opt == 4)
		{
			//create a Commoner type Object for the User 
			User<Commoner> u = new User<Commoner>(new Commoner());
			
			//setID as calcMafias(pl)+calcMafias(pl)+calcDetectives(pl)+1
			u.setID(calcHealers(pl)+calcMafias(pl)+calcDetectives(pl)+1);
			
			//replace the object of player by the user Object
			this.list.set(calcHealers(pl)+calcMafias(pl)+calcDetectives(pl), u);

			//set user with ID
			this.user = calcHealers(pl)+calcMafias(pl)+calcDetectives(pl)+1;
			System.out.print("You are Player"+u.getID()+"\nYou are a commoner. Other commoners are:[");
			
			//tell user which Player you are
			for(int i=calcMafias(pl)+calcDetectives(pl)+calcHealers(pl)+1;i<list.size();i++)
				System.out.print("Player"+this.list.get(i).getID()+" ");
			System.out.println("]");
		}
		
		//if option chosen is 5
		else if(opt == 5)
		{
			int option = 1+rand.nextInt(4);
			this.enterUser(option, pl);
		}
		
		//if option chosen is not valid choose again
		else
		{
			System.out.println("Invalid option!!!Please Enter the Option Number Again from the available Options");
		}
	}
	
	//helper function to choose the person with same ID
	private int findID(int id) {
		int j=0;
		for(j=0;j<this.list.size();j++) if(this.list.get(j).getID()==id) break;
		return j;
	}
	//helper function to give total Mafia members Combined Hp
	private int giveMafiaHp() {
		int h=0;
		for(Player p:this.list)
			if(p.getName().equals("Mafia"))
				h+=p.getHp();
		return h;
	}
		
	//helper function to give total Mafia members Combined Hp
	private int giveHealerHp() {
		int h=0;
		for(Player p:this.list)
			if(p.getName().equals("Mafia"))
				h+=p.getHp();
		return h;
	}
	
	//helper function that calculates the total Mafia
	private int calcMafias(int n) { return n/5;}
	
	//helper function that calculates the total detectives
	private int calcDetectives(int n) { return n/5;}
	
	//helper function that calculates the total healers
	private int calcHealers(int n) { return Math.max(1, n/10);}
	
	//helper function that calculates the total commoners
	private int otherPlayers(int n) { return n-(2*(n/5)+ Math.max(1, n/10));}
	
	////helper function that tells us that is Game over
	private boolean isGameOver() {	return (this.mafiaCount<=0)||(this.list.size() - this.mafiaCount == this.mafiaCount); }
	
	//function to set All players
	private void setPlayers(int players){
		
		//Mafia count
		this.mafiaCount = calcMafias(players);
		
		//Detective count
		this.detectiveCount = calcDetectives(players);
		
		//Healer count
		this.healerCount = calcHealers(players);
		
		//Commoner count
		this.commonerCount = otherPlayers(players);
		
		//initialize the list of players
		this.list = new ArrayList<Player>();
		
		//add all Mafia, Detective, Healer, Commoner
		
		//iterate over all the players
		for(int i=0;i<players;i++) {
			
			//add all Mafia
			if(i<this.mafiaCount) {
				Mafia m = new Mafia();
				m.setID(i+1);
				list.add(m);
			}
			
			//add all Detectives
			else if(i<this.mafiaCount+this.detectiveCount) {
				Detective d = new Detective();
				d.setID(i+1);
				list.add(d);
			}
			
			//add all Healers
			else if(i<this.mafiaCount+this.detectiveCount+this.healerCount) {
				Healer h = new Healer();
				h.setID(i+1);
				list.add(h);
			}
			
			//add all Commoners
			else {
				Commoner c = new Commoner();
				c.setID(i+1);
				list.add(c);
			}
		}
	}
	
	//helper function to choose all the targets
	private void mafiasChooseTarget() {
		
		//randomly choose the person
		int pl = rand.nextInt(this.list.size()-this.mafiaCount);
		while(pl>=this.list.size()||!this.list.get(pl).getName().equals("Mafia")) { pl = rand.nextInt(this.list.size()-this.mafiaCount); }
		this.list.get(pl).decreaseHp(calcMafias(this.giveMafiaHp()));
	}
	
	
	//helper function to check if the person is Mafia or not 
	private boolean isMafia(int i) {
		int j=this.findID(i);
		return j<this.list.size()&&j!=-1&&this.list.get(j).getName().equals("Mafia");
		
	}
	
	//helper function to check if the person is Detective or not
	private boolean isDetective(int i) {
		int j=this.findID(i);
		return j<this.list.size()&&j!=-1&&this.list.get(j).getName().equals("Detective");
		
	}
	
	//helper function to check if the person is Healer or not
	private boolean isHealer(int i) {
		int j=this.findID(i);
		return j<this.list.size()&&j!=-1&&this.list.get(j).getName().equals("Healer");
		
	}
	
	//helper function to check if the person is commoner or not
	private boolean isCommoner(int i) {
		int j=this.findID(i);
		return j<this.list.size()&&j!=-1&&this.list.get(j).getName().equals("Commoner");
		
	}
	
	//voting process
	private void votingProcess() {
		
		//votes casted in an array 
		int votes[] = new int[total];
		Object index[] = new Object[total];
		for (int i = 0; i < index.length; index[i++] = i);
		
		//find the index of the user in the array 
		int j=this.findID(this.user);
		if(this.user!=-1&&j<this.list.size()&&!this.list.get(j).isDead()) {
			
			//take all votes by the Mafia,Detective,Healer,Commoner. 
			this.voteByMafias(this.isMafia(this.user), votes);
			this.voteByDetectives(this.isDetective(this.user), votes);
			this.voteByHealers(this.isHealer(this.user), votes);
			this.voteByCommoners(this.isCommoner(this.user), votes);
		}
		
		else {
			
			//if user is not alive or has been voted out ask simulator to cast the votes
			this.voteByMafias(false, votes);
			this.voteByDetectives(false, votes);
			this.voteByHealers(false, votes);
			this.voteByCommoners(false, votes);
		}
		
		
		//take account of maximum votes against a person
		Arrays.sort(index,new SortByVotes());
		//sorting an array to get maximum votes
		
		//checking if more than one person has same maximum votes 
		int max_ct=0,maxi=-1,max=0;
		for(int i=0;i< this.list.size();i++) {
			
			//update the maximum votes
			if(votes[i]>max) {
				maxi=i;
				max=votes[i];
				max_ct=1;
				if((Integer)index[i]!=Integer.MAX_VALUE) index[i] = Integer.MAX_VALUE;
			}
			
			//increase count of more than one people have maximum number of votes 
			else if(votes[i]==max){
				max_ct++;
			}
		}
		
		//check if the person has to be voted out
		if(max_ct==1 && maxi!=-1) {
			this.roundStatus.add("Player"+list.get(maxi).getID()+"has been voted out");
			if(list.get(maxi).getName().equals("Mafia"))this.mafiaCount--;
			else if(list.get(maxi).getName().equals("Detective"))this.detectiveCount--;
			else if(list.get(maxi).getName().equals("Healer"))this.healerCount--;
			else if(list.get(maxi).getName().equals("Commoner"))this.commonerCount--;
			this.list.remove(maxi);
			return ;
		}
		
		// if votes are tied repeat the voting process
		else {
			System.out.println("Votes have been tied");
			this.votingProcess();
		}
	}
	
	//vote by the Mafia
	private void voteByMafias(boolean isMafia,int votes[]) {
		if(this.user!=-1&&isMafia ) {
			System.out.print("Select a Person to Vote out: ");
			
			int p=sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(p)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				p = sc.nextInt();
			}
			for(int i=0;i<this.list.size();i++)
				if(this.list.get(i).getID()==p)
					votes[i]+=(this.mafiaCount);
		}
		else {
			int max=-1;
			for(int i=0;i<this.list.size();i++) max = Math.max(max, this.list.get(i).getID());
			int p=rand.nextInt(max);			
			votes[p]+=this.mafiaCount;
		}
	}
	
	//vote by the Detectives
	private void voteByDetectives(boolean isDetective,int votes[]) {
		if(this.user!=-1&&isDetective) {
			System.out.print("Select a Person to Vote out: ");
			int p=sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(p)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				p = sc.nextInt();
			}
			
			for(int i=0;i<this.list.size();i++)
				if(this.list.get(i).getID()==p)
					votes[i]+=(this.detectiveCount);
		}
		else {
			int max=-1;
			for(int i=0;i<this.list.size();i++) max = Math.max(max, this.list.get(i).getID());
			int p=rand.nextInt(max);			
			votes[p]+=this.detectiveCount;	
		}
	}
	
	//vote by the Healers
	private void voteByHealers(boolean isHealer,int votes[]) {
		if(this.user!=-1&&isHealer) {
			System.out.print("Select a Person to Vote out: ");
			int p=sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(p)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				p = sc.nextInt();
			}
			
			for(int i=0;i<this.list.size();i++)
				if(this.list.get(i).getID()==p)
					votes[i]+=(this.healerCount);
		}
		else {
			int max=-1;
			for(int i=0;i<this.list.size();i++) max = Math.max(max, this.list.get(i).getID());
			int p=rand.nextInt(max);			
			votes[p]+=this.healerCount;	
		}
	}
	
	//vote by the Commoners
	private void voteByCommoners(boolean isCommoner,int votes[]) {
		if(this.user!=-1&&isCommoner) {
			System.out.print("Select a Person to Vote out: ");
			int p=sc.nextInt();
			
			//check if given user exists or not 
			while(!this.Exists(p)){
				System.out.println("Entered Player has been voted out or died and is Not Present. enter again: ");
				p = sc.nextInt();
			}
			
			for(int i=0;i<this.list.size();i++)
				if(this.list.get(i).getID()==p)
					votes[i]+=(this.commonerCount);
		}
		else {
			int max=-1;
			for(int i=0;i<this.list.size();i++) max = Math.max(max, this.list.get(i).getID());
			int p=rand.nextInt(max);			
			votes[p]+=this.commonerCount;	
		}
	}
}

//class MafiaApp simulator
class MafiaAppSimulator
{
	//input taking Scanner Object
	Scanner sc = new Scanner(System.in);
	RunApp run; //runApp instance 
	
	//MafiaApp Simulator
	public MafiaAppSimulator()
	{
		int pl=this.Init_App();
		this.run = new RunApp(pl);
	}
	
	//initialize the MafiaApp
	private int Init_App()
	{
		System.out.print("Welcome to Mafia\nEnter Number of Players: ");
		//take input all the players
		int players=sc.nextInt();
		
		//check for the players count constraint of minimum 6
		while(players < 6) {
			System.out.println("The minimum players required to play the game are 6. Enter Again!!");players =sc.nextInt();
		}
		System.out.print("\nChoose a Character\n1) Mafia\n2)Detective\n3)Healer\n4)Commoner\n5)AssignRandomly\n");
		return players;
	}
}
public class Assignment3_2019233 {
	public static void main(String[] args) {
		// initialize the MafiaAppSimulator object
		MafiaAppSimulator app = new MafiaAppSimulator();
	}

}
