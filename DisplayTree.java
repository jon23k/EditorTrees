package editortrees;

	

	import java.awt.Graphics;

	import javax.swing.JFrame;

	public class DisplayTree {
		
		public static void display(EditTree t)
		{
			TreeFrame frame = new TreeFrame();
			frame.root=t.getRoot();
			frame.setSize(600,600);
			frame.setVisible(true);
		}
		public static void display(Node n)
		{
			TreeFrame frame = new TreeFrame();
			frame.root=n;
			frame.setSize(600,600);
			frame.setVisible(true);
		}
		public static class TreeFrame extends JFrame
		{
			private static final long serialVersionUID = -956457703455168521L;
			public Node root;
			public int size=40;
			public void paint(Graphics g)
			{
				super.paint(g);
				int height = root.height();
				size=Math.min((int)(getWidth()/Math.pow(2,height)),(getHeight()-25)/(height+1));
				paintnode(g,root,getWidth()/2,23+size/2,getWidth()/4,(getHeight()-25-size)/height,0,0);
			}
			public void paintnode(Graphics g,Node n,int x,int y,int dx,int dy,int px,int py)
			{
				if(n==null)return;
				paintnode(g,n.left,x-dx,y+dy,dx/2,dy,x,y);
				paintnode(g,n.right,x+dx,y+dy,dx/2,dy,x,y);
				if(n!=root)
					g.drawLine(x,y,px,py);
				g.setColor(java.awt.Color.white);
				g.fillOval(x-size/2, y-size/2, size, size);
				g.setColor(java.awt.Color.black);
				g.drawOval(x-size/2, y-size/2, size, size);
				String val=n.element+" "+code(n.balance);
				g.drawString(val, x-2*(val.length()), y+6);
			}
			public String code(Node.Code c)
			{
				switch(c)
				{
				case SAME:
					return "=";
				case LEFT:
					return "/";
				case RIGHT:
					return "\\";
				default:
					return "?";
				}
			}
		}
	}


