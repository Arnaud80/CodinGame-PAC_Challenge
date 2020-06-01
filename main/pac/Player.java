//package pac;

import java.util.*;

import java.io.*;
import java.math.*;

/**
 * Grab the pellets as fast as you can!
 **/
class Player {
	public static class Action {
		public static final String MOVE="MOVE";
		public String strAction=null;
		
		public Action() {
			this.strAction="";
		}
		
		public void addAction(String action) {
			strAction+=action;
		}
		
		public String move(Pac pac, int x, int y) {
			String action="MOVE " + pac.getPacId() + " " + x + " " + y + " | ";			
			return action;
		}
		
		public void sendOrder() {
			System.out.println(strAction);
		}
		
		public void resetOrder() {
			strAction="";
		}

		public String switchPacStrongerThanOpponent(Pac pac, Pac oppPac) {
			String action=null;
			
			switch(oppPac.getType()) {
				case Pac.PAPER:
					action="SWITCH "+pac.getPacId()+" "+Pac.SCISSORS+" | ";
					break;
				case Pac.ROCK:
					action="SWITCH "+pac.getPacId()+" "+Pac.PAPER+" | ";
					break;
				case Pac.SCISSORS:
					action="SWITCH "+pac.getPacId()+" "+Pac.ROCK+" | ";
					break;
			}
			
			return action;
		}

		public String speed(Pac pac) {
			String action="SPEED "+pac.getPacId()+" | ";
			
			return action;
		}

		public String flee(Pac pac, Pac oppPac, Map map) {
			String strMove=null;
			int destX=-1;
			int destY=-1;
			int newX=-1;
			
			Position pacPosition=pac.getPosition();
			Position orgPosition=pac.getPosition();
			Position tmpPosition=pac.getPosition();
			Position oppPosition=oppPac.getPosition();
			
			for(int i=0;i<2;i++) {
				if((tmpPosition.getX()-1)<0) tmpPosition.setX(map.getWidth()-1);
				else tmpPosition.setX(pacPosition.getX()-1);
				if(map.west(pacPosition, Map.INITIAL_MAP)!=Map.WALL && 
				   !tmpPosition.equals(oppPosition) &&
				   !tmpPosition.equals(orgPosition)) {
						destX=tmpPosition.getX();
						destY=tmpPosition.getY();					
						System.err.println("destX="+destX+", destY="+destY);
				} else {
					tmpPosition.setPosition(pacPosition.getX(),pacPosition.getY());
					if((tmpPosition.getX()+1)>=map.getWidth()) tmpPosition.setX(0);
					else tmpPosition.setX(pacPosition.getX()+1);
					if(map.east(pacPosition, Map.INITIAL_MAP)!=Map.WALL && 
					   !tmpPosition.equals(oppPosition) &&
					   !tmpPosition.equals(orgPosition)) { 
							destX=tmpPosition.getX();
							destY=tmpPosition.getY();
							
							System.err.println("destX="+destX+", destY="+destY);
					} else {
						tmpPosition.setPosition(pacPosition.getX(),pacPosition.getY()-1);
						if((map.north(pacPosition, Map.INITIAL_MAP)!=Map.WALL) && 
						   (!tmpPosition.equals(oppPosition)) &&
						   (!tmpPosition.equals(orgPosition))) { 
								destX=tmpPosition.getX();
								destY=tmpPosition.getY()-1;
								System.err.println("destX="+destX+", destY="+destY);
						} else {
							tmpPosition.setPosition(pacPosition.getX(),pacPosition.getY()+1);
							if(map.south(pacPosition, Map.INITIAL_MAP)!=Map.WALL && 
							   !tmpPosition.equals(oppPosition) &&
							   !tmpPosition.equals(orgPosition)) { 
									destX=tmpPosition.getX();
									destY=tmpPosition.getY()+1;
									System.err.println("destX="+destX+", destY="+destY);
							}
						}
					}
				}
				pacPosition.setX(destX);
				pacPosition.setY(destY);
			}
			   
			if(destX!=-1 && destY!=-1)
				strMove="MOVE " + pac.getPacId() + " " + destX + " " +  destY + " | ";
			else strMove="";
			
			return strMove;
		}
	}
	
	public static class Pellets {
		private ArrayList<Pellet> pellets = new ArrayList<Pellet>();
		
		public Pellets(Map map) {
			for(int y=0;y<map.getHeight();y++) {
				for(int x=0;x<map.getWidth();x++) {
					if(map.getCellValue(x, y, Map.INITIAL_MAP)==' ') {
						pellets.add(new Pellet(x,y,1));
					}
				}
			}
		}
		
		public Pellets(Pellets pelletsA, Pellets pelletsB) {
			//for(int i=0;i<pelletsA.getPellets())
			pellets.addAll(pelletsA.getPellets());
			pellets.addAll(pelletsB.getPellets());
		}

		public Pellets() {
		}

		public Pellets(Pellets visiblePellets, Pac pac, Map map) {
			Position pacPosition=new Position(pac.getPosX(), pac.getPosY());
			Position tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			int newX=-1;
						
			while(map.north(tmpPosition, Map.INITIAL_MAP)!=Map.WALL) {
				if(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()-1)!=null) {
					pellets.add(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()-1));
				}
				tmpPosition.setY(tmpPosition.getY()-1);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			while(map.south(tmpPosition, Map.INITIAL_MAP)!=Map.WALL) {
				if(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()+1)!=null) {
					pellets.add(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()+1));
				}
				tmpPosition.setY(tmpPosition.getY()+1);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			while(map.east(tmpPosition, Map.INITIAL_MAP)!=Map.WALL && newX!=pacPosition.getX()) {
				if((tmpPosition.getX()+1)==map.getWidth()) {
					newX=0;
				} else newX=tmpPosition.getX()+1;
				
				if(visiblePellets.getPellet(newX,tmpPosition.getY())!=null) {
					pellets.add(visiblePellets.getPellet(newX,tmpPosition.getY()));
				}
				tmpPosition.setX(newX);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			newX=-1;
			while(map.west(tmpPosition, Map.INITIAL_MAP)!=Map.WALL && newX!=pacPosition.getX()) {
				if(tmpPosition.getX()==0) {
					newX=map.getWidth()-1;
				} else newX=tmpPosition.getX()-1;
				
				if(visiblePellets.getPellet(newX,tmpPosition.getY())!=null) {
					pellets.add(visiblePellets.getPellet(newX,tmpPosition.getY()));
				}
				tmpPosition.setX(newX);
			}
		}

		public void addPellet(Pellet pellet) {
			pellets.add(pellet);
		}
				
		public ArrayList<Pellet> getMaxValuedPellets() {
			ArrayList<Pellet> maxValuedPellets = new ArrayList<Pellet>();
			
			for(int i=0;i<pellets.size();i++) {
				Pellet pellet=pellets.get(i);
				
				if(pellet.getValue()==Pellet.MAX_VALUE) {
					maxValuedPellets.add(pellet);
				}
			}
			return maxValuedPellets;
		}
		
		public ArrayList<Pellet> getPellets() {
			return pellets;
		}

		public void remove(Pellet pellet) {
			pellets.remove(pellet);
		}
		
		public Pellet getNearMaxValuedPelletFromPac(Pac pac) {
			int minSumDist=Integer.MAX_VALUE;
			
			ArrayList<Pellet> maxValuedPellets=getMaxValuedPellets();
						
			Pellet pelletTarget = null;
			Pellet tmpPelletTarget = null;
			Position pacPosition=pac.getPosition();
			
			for(int i=0;i<maxValuedPellets.size();i++) {
				tmpPelletTarget=maxValuedPellets.get(i);
				int dist=pacPosition.getDistanceFrom(tmpPelletTarget.getPosition());
				
				if(dist<minSumDist) {
					minSumDist=dist;
					pelletTarget=tmpPelletTarget;
				}
			}
			
			return pelletTarget;
		}
		
		public Pellet getNearPelletFromPosition(Position position) {
			int minDistX=Integer.MAX_VALUE;
			int minDistY=Integer.MAX_VALUE;
			int minSumDist=Integer.MAX_VALUE;
						
			Pellet pelletTarget = null;
			Pellet tmpPelletTarget = null;
			
			for(int i=0;i<pellets.size();i++) {
				tmpPelletTarget=pellets.get(i);
				int dist=position.getDistanceFrom(tmpPelletTarget.getPosition());
								
				if(dist<minSumDist) {
					minSumDist=dist;
					pelletTarget=tmpPelletTarget;
				}
			}
			
			return pelletTarget;
		}
		
		public Pellet getNearPelletFromPac(Pac pac) {
			int minSumDist=Integer.MAX_VALUE;
						
			Pellet pelletTarget = null;
			Pellet firstPelletTarget = null;
			Pellet tmpPelletTarget = null;
			Position pacPosition=pac.getPosition();
			int iteration=1;
			int dist=0;
			boolean newStep=true;
			
			System.err.println("Search near pellet for pac "+pac.getPacId());
			while(iteration<=2) {
				for(int i=0;i<pellets.size();i++) {
					tmpPelletTarget=pellets.get(i);
					dist=pacPosition.getDistanceFrom(tmpPelletTarget.getPosition());
									
					if(dist<minSumDist && firstPelletTarget!=tmpPelletTarget) {
						minSumDist=dist;
						pelletTarget=tmpPelletTarget;
					}
				}
				// If Pac can move 2 times and distance from pellet < 2, we remove the first target and search the seconde one
				if(pac.getSpeedTurnsLeft()>0 && minSumDist==1 && pelletTarget!=null) {
					System.err.println("Prepare for 2nd iteration");
					pacPosition.setPosition(pelletTarget.getPosX(), pelletTarget.getPosY());
					firstPelletTarget=pelletTarget;
					minSumDist=Integer.MAX_VALUE;
				}
				iteration++;
			}
						
			return pelletTarget;
		}

		public void remove(int posX, int posY) {
			for(int i=0;i<pellets.size();i++) {
				if(pellets.get(i).getPosX()==posX && pellets.get(i).getPosY()==posY) pellets.remove(i);
			}			
		}

		public void display() {
			for(int i=0;i<pellets.size();i++) {
				System.err.println("("+pellets.get(i).getPosX()+","+pellets.get(i).getPosY()+"),");
			}
		}

		public void remove(String strMove) {
			System.err.println("strMove="+strMove);
			int posX=Integer.parseInt(strMove.split(" ")[2]);
			int posY=Integer.parseInt(strMove.split(" ")[3]);
			
			System.err.println("remove targeted Pellet "+posX+","+posY);
			remove(posX, posY);
		}

		public void updateFromPacVisibility(Pac pac, Map map, Pellets visiblePellets) {
			Position pacPosition=new Position(pac.getPosX(), pac.getPosY());
			Position tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			int newX=-1;
			
			while(map.north(tmpPosition, Map.INITIAL_MAP)!=Map.WALL) {
				if(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()-1)==null) {
					remove(tmpPosition.getX(),tmpPosition.getY()-1);
				}
				tmpPosition.setY(tmpPosition.getY()-1);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			while(map.south(tmpPosition, Map.INITIAL_MAP)!=Map.WALL) {
				if(visiblePellets.getPellet(tmpPosition.getX(),tmpPosition.getY()+1)==null) {
					remove(tmpPosition.getX(),tmpPosition.getY()+1);
				}
				tmpPosition.setY(tmpPosition.getY()+1);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			while(map.east(tmpPosition, Map.INITIAL_MAP)!=Map.WALL && !tmpPosition.equals(pacPosition)) {
				if((tmpPosition.getX()+1)==map.getWidth()) {
					newX=0;
				} else newX=tmpPosition.getX()+1;
				
				if(visiblePellets.getPellet(newX,tmpPosition.getY())==null) {
					remove(newX,tmpPosition.getY());
				}
				tmpPosition.setX(newX);
			}
			
			tmpPosition=new Position(pac.getPosX(), pac.getPosY());
			while(map.west(tmpPosition, Map.INITIAL_MAP)!=Map.WALL && !tmpPosition.equals(pacPosition)) {
				if((tmpPosition.getX()-1)==-1) {
					newX=map.getWidth()-1;
				} else newX=tmpPosition.getX()-1;
				
				if(visiblePellets.getPellet(newX,tmpPosition.getY())==null) {
					remove(newX,tmpPosition.getY());
				}
				tmpPosition.setX(newX);
			}
		}

		public Pellet getPellet(int x, int y) {
			Pellet result=null;
			for(int i=0;i<pellets.size();i++) {
				if(pellets.get(i).getPosX()==x && pellets.get(i).getPosY()==y) result=pellets.get(i);
			}
			return result;
		}
	}
	
	public static class Pellet {
		public static final int MAX_VALUE=10;
		private int posX;
		private int posY;
		private int value;
		
		public Pellet(int posX, int posY, int value ) {
			this.posX=posX;
			this.posY=posY;
			this.value=value;
		}
		
		public Position getPosition() {
			Position position=new Position(posX, posY);
			return position;
		}
		
		public int getPosX() {
			return posX;
		}

		public void setPosX(int posX) {
			this.posX = posX;
		}

		public int getPosY() {
			return posY;
		}

		public void setPosY(int posY) {
			this.posY = posY;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
	
	public static class Pacs {
		private ArrayList<Pac> pacs = new ArrayList<Pac>();
		
		public void addPac(Pac pac) {
			pacs.add(pac);
		}
		
		public Pac getMyPacById(int id) {
			Pac pac=null;
			Pac tmpPac=null;
			
			for(int i=0;i<pacs.size();i++) {
				tmpPac=pacs.get(i);
				if(tmpPac.getPacId()==id && tmpPac.isMine()) pac=pacs.get(i);
			}
			return pac;
		}
		
		public void display() {
			for(int i=0;i<pacs.size();i++) {
				System.err.println("Pac("+pacs.get(i).getPacId()+","+pacs.get(i).isMine()+","+pacs.get(i).getType()+")");
			}
		}
		
		public ArrayList<Pac> getMyPacs() {
			ArrayList<Pac> myPacs = new ArrayList<Pac>();
			
			for(int i=0;i<pacs.size();i++) {
				Pac pac=pacs.get(i);
				
				if(pac.isMine() && !pac.isDead()) {
					myPacs.add(pac);
				}
			}
			return myPacs;
		}

		public Pac getNearOppPacFromPac(Pac pac) {
			int minDistX=999;
			int minDistY=999;
			int minSumDist=minDistX+minDistY;
			
			Pac nearOppPac=null;
			Pac tmpNearPac=null;
			
			for(int i=0;i<pacs.size();i++) {
				if(!pacs.get(i).isMine() && !pacs.get(i).getType().equals(Pac.DEAD)) {
					tmpNearPac=pacs.get(i);
					
					int distX=Math.abs(tmpNearPac.getPosX()-pac.getPosX());
					int distY=Math.abs(tmpNearPac.getPosY()-pac.getPosY());
					
					if((distX+distY)<minSumDist) {
						minSumDist=distX+distY;
						nearOppPac=tmpNearPac;
					}
				}
			}
			return nearOppPac;
		}
		
		public Pac getMyNearPacFromPellet(Pellet pellet) {
			int minDistX=999;
			int minDistY=999;
			int minSumDist=minDistX+minDistY;
			
			Pac nearPac=null;
			Pac tmpNearPac=null;
			
			for(int i=0;i<pacs.size();i++) {
				if(pacs.get(i).isMine() && !pacs.get(i).isDead()) {
					tmpNearPac=pacs.get(i);
					
					int distX=Math.abs(tmpNearPac.getPosX()-pellet.getPosX());
					int distY=Math.abs(tmpNearPac.getPosY()-pellet.getPosY());
					
					if((distX+distY)<minSumDist) {
						minSumDist=distX+distY;
						nearPac=tmpNearPac;
					}
				}
			}
			return nearPac;
		}

		public boolean isFirstPlayer() {
			return pacs.get(0).isMine();
		}
	}
	
	public static class Pac {
		public static final String ROCK="ROCK";
		public static final String PAPER="PAPER";
		public static final String SCISSORS="SCISSORS";
		public static final String DEAD="DEAD";
		
		private int pacId;
		private boolean mine;
		private int posX;
		private int posY;
		private String type;
		private int speedTurnsLeft;
		private int abilityCoolDown;
		private String lastAction=null;
		
		public Pac(int pacId, boolean mine, int posX, int posY, String type, int speedTurnsLeft, int abilityCoolDown) {
			this.pacId=pacId;
			this.mine=mine;
			this.posX=posX;
			this.posY=posY;
			this.type=type;
			this.speedTurnsLeft=speedTurnsLeft;
			this.abilityCoolDown=abilityCoolDown;
		}
		
		public void setLastAction(String action) {
			lastAction=action;
		}
		
		public String getLastAction() {
			return lastAction;
		}

		public int getPacId() {
			return pacId;
		}

		public void setPacId(int pacId) {
			this.pacId = pacId;
		}

		public boolean isMine() {
			return mine;
		}
		
		public boolean isDead() {
			boolean result=false;
			
			if(this.type.equals(Pac.DEAD)) result=true;
			
			return result;
		}

		public void setMine(boolean mine) {
			this.mine = mine;
		}

		public int getPosX() {
			return posX;
		}

		public void setPosX(int posX) {
			this.posX = posX;
		}

		public int getPosY() {
			return posY;
		}

		public void setPosY(int posY) {
			this.posY = posY;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public int getSpeedTurnsLeft() {
			return speedTurnsLeft;
		}

		public void setSpeedTurnsLeft(int speedTurnsLeft) {
			this.speedTurnsLeft = speedTurnsLeft;
		}

		public int getAbilityCoolDown() {
			return abilityCoolDown;
		}

		public void setAbilityCoolDown(int abilityCoolDown) {
			this.abilityCoolDown = abilityCoolDown;
		}

		public boolean isStrongerThan(Pac oppPac) {
			boolean isStronger=false;
			
			switch(oppPac.getType()) {
				case PAPER:
					if(type.equals(SCISSORS)) isStronger=true;
					break;
				case ROCK:
					if(type.equals(PAPER)) isStronger=true;
					break;
				case SCISSORS:
					if(type.equals(ROCK)) isStronger=true;
					break;
			}
			
			return isStronger;
		}

		public int getDistanceFrom(Pac pac) {
			int distX=Math.abs(pac.getPosX()-posX);
			int distY=Math.abs(pac.getPosY()-posY);
			
			return distX+distY;
		}

		public int getDistanceFrom(int x, int y) {
			int distX=Math.abs(x-posX);
			int distY=Math.abs(y-posY);
			
			return(distX+distY);
		}
		
		public int getDistanceFrom(Pellet pellet) {
			int distX=Math.abs(pellet.getPosX()-posX);
			int distY=Math.abs(pellet.getPosY()-posY);
			
			return(distX+distY);
		}

		public Position getPosition() {
			Position position=new Position(posX, posY);
			return position;
		}
	}
	
	public static class Position {
	    private int x=0;
	    private int y=0;
	    
	    public Position() {
	        this.x = 0;
	        this.y = 0;
	    }
	    
	    @Override
	    public boolean equals(Object object)
	    {
	        boolean same = false;
	        
	        if (object instanceof Position)
	        	if(this.x == ((Position)object).x && this.y == ((Position)object).y) same=true;

	        return same;
	    }
	    
	    @Override
	    public int hashCode() {
	    	return((31+x)*(31+y));
	    }
	    
	    public Position(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	    
	    public Position(Position position) {
	        this.x = position.getX();
	        this.y = position.getY();
	    }
	    
	    public void setPosition(int newX, int newY) {
	        this.x = newX;
	        this.y = newY;
	    }
	    
	    public int getDistanceFrom(Position position) {
	        int distX=0;
	        int distY=0;
	        
	        distX=Math.abs(x-position.getX());
	        distY=Math.abs(y-position.getY());
	        
	        return(distX+distY);
	    }
	    
	    public int getX() {
	        return x;
	    }
	    
	    public int getY() {
	        return y;
	    }
	    
	    public void setX(int newX) {
	        this.x=newX;
	    }
	    
	    public void setY(int newY) {
	        this.y=newY;
	    }	    
	}
	
	public static class SolutionPath {
		private ArrayList<Position> steps = new ArrayList<Position>();
		private int distMin;

		public SolutionPath(int distMin, ArrayList<Position> steps) {
			this.distMin=distMin;
			int size=steps.size();
			
			for(int i=0;i<size;i++) {
				this.steps.add(new Position(steps.get(i).getX(),steps.get(i).getY()));
			}
		}
		
		public ArrayList<Position> getSteps() {
			return steps;
		}
		
		public int getDistMin() {
			return distMin;
		}
	}
	
	public static class Path {
		private static final Integer MAX_DEPTH=50;
/*		private ArrayList<Position> positions = new ArrayList<Position>();
		
		public void addStep(Position position) {
			positions.add(position);
		}
		
		public void addSteps(Path path) {
			positions.addAll(path.getPositions());
		}
		
		public int size() {
			return positions.size();
		}
		
		public ArrayList<Position> getPositions() {
			return positions;
		}
		
		public Position get(int index) {
			return positions.get(index);
		}*/
		
		public Path() {
			
		}
		
		public void displayPath(ArrayList<Position> steps) {
			for(int i=0;i<steps.size();i++) {
				System.err.println("("+steps.get(i).getX()+","+steps.get(i).getY()+")");
			}
		}
		
		public SolutionPath getShorterPathSolution(ArrayList<SolutionPath> solutions) {
			SolutionPath shorter=null;
			int distMin=Integer.MAX_VALUE;
			
			for(int i=0;i<solutions.size();i++) {
				if(solutions.get(i).getDistMin()<distMin) {
					distMin=solutions.get(i).getDistMin();
					shorter=solutions.get(i);
				}
			}
			return shorter;
		}
		
		public boolean searchPath(Map map, Position from, Position destination, ArrayList<Position> steps) {
			if(from.getX()>=0 && from.getX()<map.getWidth() &&
			   from.getY()>=0 && from.getY()<map.getHeight() &&
			   from.equals(destination)) {
				map.setCellValue(from, Map.PATHFINDER_MAP, Map.STEP);
				return true;
			}
			
			if(from.getX()>=0 && from.getX()<map.getWidth() &&
			   from.getY()>=0 && from.getY()<map.getHeight() &&
			   map.getCellValue(from.getX(), from.getY(), Map.PATHFINDER_MAP)==Map.FLOOR) {
				map.setCellValue(from, Map.PATHFINDER_MAP, Map.STEP);
				
				// Try by the North
				if(searchPath(map, new Position(from.getX(), from.getY()-1), destination, steps)) {
					steps.add(new Position(from.getX(),from.getY()-1));
					return true;
				}
				
				// Try by the East
				if(searchPath(map, new Position(from.getX()+1, from.getY()), destination, steps)) {
					steps.add(new Position(from.getX()+1,from.getY()));
					return true;
				}
				
				// Try by the South
				if(searchPath(map, new Position(from.getX(), from.getY()+1), destination, steps)) {
					steps.add(new Position(from.getX(),from.getY()+1));
					return true;
				}
				
				// Try by the West
				if(searchPath(map, new Position(from.getX()-1, from.getY()), destination, steps)) {
					steps.add(new Position(from.getX()-1,from.getY()));
					return true;
				}

			}
			
			return false;
		}
		
		public int searchShortPath(Map map, Position from, Position destination, ArrayList<Position> steps, int distMin, int dist, ArrayList<SolutionPath> solutions) {
			if(from.equals(destination)) { 	
				//map.setCellValue(from, Map.PATHFINDER_MAP, 'X');
				//map.display(Map.PATHFINDER_MAP);
				
				Position finalStep=new Position(from.getX(),from.getY());
				
				steps.add(finalStep);
				SolutionPath solution=new SolutionPath(dist+1, steps);

				solutions.add(solution);
				//System.err.println("Added solution #"+solutions.size());
				
				//map.setCellValue(from, Map.PATHFINDER_MAP, Map.FLOOR);
				steps.remove(finalStep);
				
				return Integer.min(distMin, dist);
			} else if(dist==distMin ) {
				return distMin;
			}
			
			if((from.getY()>0) && (from.getY()<(map.getHeight()-1)) &&
			   (map.getCellValue(from.getX(), from.getY(), Map.PATHFINDER_MAP)==Map.FLOOR)) {
				
				//System.err.println("step "+dist+" : ("+from.getX()+","+from.getY()+")="+(char)map.getCellValue(from.getX(), from.getY(), Map.PATHFINDER_MAP));
				
				Position step=new Position(from.getX(),from.getY());
				steps.add(step);
				map.setCellValue(from, Map.PATHFINDER_MAP, Map.STEP);
				
				// Try by the North
				int destX=from.getX();
				int destY=from.getY()-1;
				//System.err.println("Test ("+destX+","+destY);
				distMin=searchShortPath(map, new Position(destX, destY), destination, steps, distMin, dist+1, solutions);
				
				
				// Try by the South
				destX=from.getX();;
				destY=from.getY()+1;
				//System.err.println("Test ("+destX+","+destY);
				distMin=searchShortPath(map, new Position(destX, destY), destination, steps, distMin, dist+1, solutions);
				
				// Try by the East
				if((from.getX()+1)==map.width) destX=0; 
				else destX=from.getX()+1;
				destY=from.getY();
				//System.err.println("Test ("+destX+","+destY);
				distMin=searchShortPath(map, new Position(destX, destY), destination, steps, distMin, dist+1, solutions);
				
				
				// Try by the West
				if((from.getX()-1)<0) destX=map.width-1;
				else destX=from.getX()-1;
				destY=from.getY();
				//System.err.println("Test ("+destX+","+destY);
				distMin=searchShortPath(map, new Position(destX, destY), destination, steps, distMin, dist+1, solutions);
				
				map.setCellValue(step, Map.PATHFINDER_MAP, Map.FLOOR);
				steps.remove(step);
			}
						
			return distMin;
		}
	}
	
	public static class Map {
		public static final String INITIAL_MAP="Initial";
		public static final String PATHFINDER_MAP="PathFinderMap";
		public static final char WALL='#';
		public static final char FLOOR=' ';
		public static final char STEP='S';
	    private int width;
	    private int height;
	    private int initialMap[][];
	    private int pathfinderMap[][];
	    
	    public Map(int width, int height) {
	        this.width=width;
	        this.height=height;
	        this.initialMap = new int[width][height];
	        this.pathfinderMap = new int[width][height];
	    }	    
	    
		public int getWidth() {
	        return width;
	    }
	    
	    public int getHeight() {
	        return height;
	    }
	    	    
	    public void setLine(int y, int value, String layer) {
	        switch(layer) {
	            case PATHFINDER_MAP:
	                for(int x=0;x<width;x++) {
	                    initialMap[x][y]=value;
	                }
	                break;	        }
	    }
	    
	    public void setLine(int y, String line) {
	        for(int x=0;x<width;x++) {
	            initialMap[x][y]=line.charAt(x);
	        }
	    }
	    
	    public void initPathfinderMap() {
	    	pathfinderMap=initialMap.clone();
	    }
	    
	    public void display(String layer) {
	        String line="";
	        
	        switch(layer) {
	            case Map.INITIAL_MAP:
	                for(int y=0;y<height;y++) {
	                    line="";
	                    for(int x=0;x<width;x++) {
	                        line=line + " " + (char)initialMap[x][y];
	                    }
	                    System.err.println(line);
	                }
	                break;
	            case Map.PATHFINDER_MAP:
	                for(int y=0;y<height;y++) {
	                    line="";
	                    for(int x=0;x<width;x++) {
	                        line=line + " " + (char)pathfinderMap[x][y];
	                    }
	                    System.err.println(line);
	                }
	                break;
	        }
	    }
	    	    
	    public int north(Position position, String layer) {
	        int newY=position.getY()-1;
	        int value=0;
	        
	        if(newY < 0) {
	            switch(layer) {
	                case INITIAL_MAP:
	                    // If outside the map, we return Map.WALL as Island
	                    value=Map.WALL;
	                    break;
	            }
	        }
	        else {
	            //System.err.println("DEBUG computedMap["+position.getX()+"]["+newY+"]="+computedMap[position.getX()][newY]);
	            switch(layer) {
	                case INITIAL_MAP:
	                    value=initialMap[position.getX()][newY];
	                    break;
	            }
	        }
	     
	        return value;
	    }
	    
	    public int west(Position position, String layer) {
	        int newX=position.getX()-1;
	        int value=0;
	        
	        if(newX < 0) {
	            switch(layer) {
	                case INITIAL_MAP:
	                    // If outside the map, we return the opposite Value to throw the map
	                    value=initialMap[width-1][position.getY()];
	                    break;
	            }
	        } else {
	            //System.err.println("DEBUG computedMap["+newX+"]["+position.getY()+"]="+computedMap[newX][position.getY()]);
	            switch(layer) {
	                case INITIAL_MAP:
	                    value=initialMap[newX][position.getY()];
	                    break;
	            }
	        }
	        return value;
	    }
	    
	    public int south(Position position, String layer) {
	        int newY=position.getY()+1;
	        int value=0;
	        
	        if(newY >= height) {
	            switch(layer) {
	                case INITIAL_MAP:
	                    // If outside the map, we return Map.WALL as Island
	                    value=Map.WALL;
	                    break;
	            }
	        } else {
	            switch(layer) {
	                case INITIAL_MAP:
	                    value=initialMap[position.getX()][newY];
	                    break;
	            }
	        }
	        return value;
	    }
	    
	    public int east(Position position, String layer) {
	        int newX=position.getX()+1;
	        int value=0;
	        
	        if(newX >= width) {
	            switch(layer) {
	                case INITIAL_MAP:
	                    // If outside the map,
	                    value=initialMap[0][position.getY()];
	                    break;
	            }
	        } else {
	            switch(layer) {
	                case INITIAL_MAP:
	                    value=initialMap[newX][position.getY()];
	                    break;
	            }
	        }
	        return value;
	    }
	    
	    public int[][] getArrayMap() {
	    	return initialMap.clone();
	    }
	    
	    public int getCellValue(int x, int y, String layer) {
	        int value=0;
	        
	        switch(layer) {
	            case Map.INITIAL_MAP:
	                value=initialMap[x][y];
	                break;
	            case Map.PATHFINDER_MAP:
	                value=pathfinderMap[x][y];
	                break;
	        }
	        return value;
	    }
	    
	    public void setCellValue(int x, int y, String layer, int value) {
	        switch(layer) {
	            case Map.INITIAL_MAP:
	                initialMap[x][y]=value;
	                break;
	            case Map.PATHFINDER_MAP:
	                pathfinderMap[x][y]=value;
	                break;
	        }
	    }
	    
	    public void setCellValue(Position position, String layer, int value) {
	        switch(layer) {
	            case INITIAL_MAP:
	                initialMap[position.getX()][position.getY()]=value;
	                break;
	            case PATHFINDER_MAP:
	                pathfinderMap[position.getX()][position.getY()]=value;
	                break;
	        }
	    }
	}

    public static void main(String args[]) {
    	long startTime = System.nanoTime();
        Scanner in = new Scanner(System.in);
        int width = in.nextInt(); // size of the grid
        int height = in.nextInt(); // top left corner is (x=0, y=0)
        
        int distMaxBeforeSwitch=1;
        
        Map map=new Map(width, height);
        Path path=new Path();
        
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < height; i++) {
            String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
            map.setLine(i, row);
        }
        
        // Initialize the PathFinderMap
        map.initPathfinderMap();
                
        // Get all potential pellets
        Pellets pellets = new Pellets(map);
        Pacs oldPacs = null;
        Pacs pacs=null;

        // game loop
        while (true) {
        	if(pacs!=null) oldPacs=pacs;
        	
        	pacs = new Pacs();
        	
            Pellets visiblePellets = new Pellets();
            
            int myScore = in.nextInt();
            int opponentScore = in.nextInt();
            int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
            for (int i = 0; i < visiblePacCount; i++) {
                int pacId = in.nextInt(); // pac number (unique within a team)
                boolean mine = in.nextInt() != 0; // true if this pac is yours
                int x = in.nextInt(); // position in the grid
                int y = in.nextInt(); // position in the grid
                String typeId = in.next(); // unused in wood leagues
                int speedTurnsLeft = in.nextInt(); // unused in wood leagues
                int abilityCooldown = in.nextInt(); // unused in wood leagues
                
                Pac newPac=new Pac(pacId, mine, x, y, typeId, speedTurnsLeft, abilityCooldown);
                // If my Pac we save the last action
                if(mine && oldPacs!=null) {
                	Pac tmpPac=oldPacs.getMyPacById(newPac.getPacId());
                	newPac.setLastAction(tmpPac.getLastAction());
                }
                pacs.addPac(newPac);
                
                
                pellets.remove(x, y);
            }
                        
            System.err.println("distMax="+distMaxBeforeSwitch);
            
            int visiblePelletCount = in.nextInt(); // all pellets in sight
            
            for (int i = 0; i < visiblePelletCount; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int value = in.nextInt(); // amount of points this pellet is worth
                
                visiblePellets.addPellet(new Pellet(x, y, value));
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            Action action=new Action();
            
            ArrayList<Pac> myPacs=pacs.getMyPacs();
            
         // Remove Pellets confirmed has removed by VisibleMap from Pac
            
            for(int i=0;i<myPacs.size();i++) {
            	Pac pac=myPacs.get(i);
            	pellets.updateFromPacVisibility(pac, map, visiblePellets);
            }
            
            Pellets targetPellets=new Pellets(visiblePellets,pellets);
            
            boolean otherWait=false;
            for(int i=0;i<myPacs.size();i++) {
            	Pac pac=myPacs.get(i);
            	Pac oldPac=null;;
            	String strSwitch=null;
            	String strSpeed=null;
            	String strMove=null;
            	boolean canKill=false;
            	boolean canKillMe=false;
            	
            	System.err.println("Prepare Pac #"+pac.getPacId()+" action");
            	
            	// Create Pellets list visible from the Pac
            	Pellets visiblePelletsFromPac=new Pellets(visiblePellets, pac, map);
            	
            	if(oldPacs!=null) oldPac=oldPacs.getMyPacById(pac.getPacId());
            	
            	Pellet nearMaxPellet=null;
            	Pellet nearVisiblePellet=null;
            	Pellet nearInvisiblePellet=null;
            	Pellet targetPellet=null;
            	System.err.println("0");
            	nearMaxPellet = targetPellets.getNearMaxValuedPelletFromPac(pac);
            	System.err.println("1");
            	nearVisiblePellet = visiblePelletsFromPac.getNearPelletFromPac(pac);
            	System.err.println("2");
            	nearInvisiblePellet = targetPellets.getNearPelletFromPac(pac);
            	System.err.println("3");
            	

            	if(nearMaxPellet!=null) {
            		System.err.println("nearMaxPellet = "+nearMaxPellet.getPosX()+","+nearMaxPellet.getPosY());	
	            	
	            	// If an another Pac is near for this big Pellet we let for him
	            	if(pacs.getMyNearPacFromPellet(nearMaxPellet).getPacId()!=pac.getPacId()) {
	            		System.err.println("We let the big pellet for pac #"+pacs.getMyNearPacFromPellet(nearMaxPellet).getPacId());
	            	} else {
	            		targetPellet=nearMaxPellet;
	            	}
            	} else System.err.println("No maxPellet");
            	
            	if(targetPellet==null && nearVisiblePellet!=null) {
            		System.err.println("nearVisiblePellet = "+nearVisiblePellet.getPosX()+","+nearVisiblePellet.getPosY());
	            	
	            	if(pacs.getMyNearPacFromPellet(nearVisiblePellet).getPacId()!=pac.getPacId()) {
	            		System.err.println("We let the visible for pac #"+pacs.getMyNearPacFromPellet(nearVisiblePellet).getPacId());
	            	} else {
	            		targetPellet=nearVisiblePellet;
	            	}
            	} else System.err.println("No visible pellet");
            	
            	if(targetPellet==null && nearInvisiblePellet!=null) {
            		targetPellet=nearInvisiblePellet;
            	}
            	
            	Pac oppPac=null;
            	oppPac = pacs.getNearOppPacFromPac(pac);
            	
            	// TODO : Manage the case where 2 pacs are as a same distance from Pac
            	if(oppPac!=null) 
            	{
            		System.err.println("oppPac #"+oppPac.getPacId()+" is neared with type "+oppPac.getType());
            		if(oppPac.getDistanceFrom(pac)==1 && pac.isStrongerThan(oppPac) && oppPac.getAbilityCoolDown()>0) {
            			System.err.println("Pac #"+pac.getPacId()+" can Kill oppPac #"+oppPac.getPacId());
            			canKill=true;
            		}
            		if(oppPac.getDistanceFrom(pac)==1 && oppPac.isStrongerThan(pac) && pac.getAbilityCoolDown()>0) {
            			System.err.println("oppPac #"+oppPac.getPacId()+" can kill myPac #"+pac.getPacId());
            			canKillMe=true;
            		}
            	}
            	if(pac.getAbilityCoolDown()==0 && !canKill) {
            		if(oppPac!=null && !pac.isStrongerThan(oppPac) && oppPac.getDistanceFrom(pac)<=distMaxBeforeSwitch) {
                		strSwitch=action.switchPacStrongerThanOpponent(pac,oppPac);
            			action.addAction(strSwitch);
            			
            			pac.setLastAction(strSwitch);
                	} else if(nearMaxPellet!=null || pac.getDistanceFrom(targetPellet)>2) {
                		strSpeed=action.speed(pac);
                		action.addAction(strSpeed);
                		
                		pac.setLastAction(strSpeed);
                	}
            	} 
            	
            	// Manage the movement if there is no action Switch or Speed
            	if(strSwitch==null && strSpeed==null) {
            		// If I can kill, I move on the oppOpponent position
            		if(canKill) {
            			System.err.println("Pac " + pac.getPacId() + " will kill " + oppPac.getPacId());
            			strMove=action.move(pac, oppPac.getPosX(), oppPac.getPosY());
            		}
            		// If He can kill me, I flee !
            		else if(canKillMe) {
            			System.err.println("Pac " + pac.getPacId() + " must flee " + oppPac.getPacId());
            			System.err.println("Pac " + pac.getPacId() + " have speedTurnLeft=" + pac.getSpeedTurnsLeft());
            			strMove=action.flee(pac, oppPac, map);
            		}
            		// Else I move on pellet position
            		else {	
	    				ArrayList<Position> steps=new ArrayList<Position>();
	    				ArrayList<SolutionPath> solutions=new ArrayList<SolutionPath>();
	    				
	    				path.searchShortPath(map, pac.getPosition(), targetPellet.getPosition(), steps, Path.MAX_DEPTH, 0, solutions);
	    				
	    				SolutionPath shorterPath=path.getShorterPathSolution(solutions);
	    				
	    				// TODO : Manage the case where 2 pacs are as a same distance from Pac
	    				if(solutions.size()!=0) {
	    					if(shorterPath.getSteps().size()>2 && pac.getSpeedTurnsLeft()>0 && oppPac!=null) {
	    						// If oppPac position is equal at 2nd step of the pac and oppPac is Stronger, we just move on the 1st step.
	    						if(oppPac.getPosition().equals(shorterPath.getSteps().get(1))) {
	    							path.displayPath(shorterPath.getSteps());
	    							if (!pac.isStrongerThan(oppPac) || (pac.getType().equals(oppPac.getType()) && oppPac.getAbilityCoolDown()==0)) {
	    								strMove="";
	    							}
	    						} else if(oppPac.getPosition().equals(shorterPath.getSteps().get(2)) && pac.getSpeedTurnsLeft()>0) {
	    							if (!pac.isStrongerThan(oppPac) || (pac.getType().equals(oppPac.getType()) && oppPac.getAbilityCoolDown()==0)) {
	    								strMove=action.move(pac, shorterPath.getSteps().get(1).getX(), shorterPath.getSteps().get(1).getY());
	    							}
	    						}
	    					}
	    				} else System.err.println("No path found !");
	    				if(strMove==null) strMove=action.move(pac, targetPellet.getPosX(), targetPellet.getPosY());
            			/*
            			else {
            				System.err.println("Pac " + pac.getPacId() + " must move to near visible pellet at ("+nearPellet.getPosX()+","+nearPellet.getPosY()+")");
            				
            				ArrayList<Position> steps=new ArrayList<Position>();
            				ArrayList<SolutionPath> solutions=new ArrayList<SolutionPath>();
            				
            				path.searchShortPath(map, pac.getPosition(), nearVisiblePellet.getPosition(), steps, Path.MAX_DEPTH, 0, solutions);
            				
            				SolutionPath shorterPath=path.getShorterPathSolution(solutions);
            				
            				// TODO : Manage the case where 2 pacs are as a same distance from Pac
            				if(solutions.size()!=0) {
            					System.err.println("SpeedTurnsLeft="+pac.getSpeedTurnsLeft());
            					System.err.println("shorterPath.getSteps().size()="+shorterPath.getSteps().size());
            					if(shorterPath.getSteps().size()>2 && oppPac!=null) {
            						// If oppPac position is equal at 2nd step of the pac and oppPac is Stronger, we just move on the 1st step.
            						if(oppPac.getPosition().equals(shorterPath.getSteps().get(1))) {
            							path.displayPath(shorterPath.getSteps());
            							if (!pac.isStrongerThan(oppPac) || (pac.getType().equals(oppPac.getType()) && oppPac.getAbilityCoolDown()==0)) {
            								System.err.println("Cancel the move");
            								strMove="";
            							}
            							System.err.println("Step 2 : "+shorterPath.getSteps().get(2).getX()+","+shorterPath.getSteps().get(2).getY());
            						} else if(oppPac.getPosition().equals(shorterPath.getSteps().get(2)) && pac.getSpeedTurnsLeft()>0) {
            							System.err.println("Found oppPac on the 2nd step");
            							if (!pac.isStrongerThan(oppPac) || (pac.getType().equals(oppPac.getType()) && oppPac.getAbilityCoolDown()==0)) {
            								strMove=action.move(pac, shorterPath.getSteps().get(1).getX(), shorterPath.getSteps().get(1).getY());
            								System.err.println("Move on first step : "+strMove);
            							}
            						} 
            					} 
            				} else System.err.println("No path found !");
            				if(strMove==null) strMove=action.move(pac, nearVisiblePellet.getPosX(), nearVisiblePellet.getPosY());
                    	}*/
            		}
            		
            		// Validate the move and Manage collision
            		if(oldPac!=null) {
            			if(pac.getPosition().equals(oldPac.getPosition()) && oldPac.getLastAction().contains("MOVE") && !otherWait) {
            				System.err.println("Pac "+pac.getPacId()+" was in collision, he will wait");
                			pac.setLastAction("");
                			otherWait=true;
                		} else {
                			action.addAction(strMove);
                			pac.setLastAction(strMove);
                		}
            		} else {
            			action.addAction(strMove);
            			pac.setLastAction(strMove);
            		}
            		if(!strMove.equals("")) targetPellets.remove(strMove);
            	}
            }

            action.sendOrder();
            long endTime = System.nanoTime();
    		long timeElapsed = endTime - startTime;
    		System.err.println("Execution time in milliseconds: " + timeElapsed / 1000000);
        }
    }
}