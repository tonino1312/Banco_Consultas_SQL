/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bancobbdd;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author Charlie
 */
public class ImagenToogle extends JPanel{
    private Image imagenFondo;

      public ImagenToogle(String rutaImagen) {
        super();
        imagenFondo = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
    }

    public void setImagenFondo(String rutaImagen) {
        imagenFondo = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
        repaint();
    }
}
