package com.vandrugg.docdispatch;

import java.util.Scanner;

import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracionCorreo;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.repository.RepositorioHistorialEnvios;
import com.vandrugg.docdispatch.security.CifradorCredenciales;
import com.vandrugg.docdispatch.security.ProtectorClave;
import com.vandrugg.docdispatch.security.ProtectorClaveWindowsDpapi;
import com.vandrugg.docdispatch.service.ServicioCorreo;
import com.vandrugg.docdispatch.service.ServicioDocumentos;
import com.vandrugg.docdispatch.service.ServicioPreparacionEnvios;
import com.vandrugg.docdispatch.service.ServicioCorreoDiferido;
import com.vandrugg.docdispatch.service.ServicioCredencialesCorreo;
import com.vandrugg.docdispatch.ui.MenuConfiguracion;
import com.vandrugg.docdispatch.ui.MenuDestinatarios;
import com.vandrugg.docdispatch.ui.MenuHistorial;
import com.vandrugg.docdispatch.ui.MenuPrincipal;
import com.vandrugg.docdispatch.ui.MenuProcesamientoDocumentos;

public class Main {

        public static void main(String[] args) {

                DatabaseManager databaseManager = DatabaseManager.produccion();
                databaseManager.inicializarBaseDeDatos();

                RepositorioDestinatarios repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

                RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracion(databaseManager);

                RepositorioHistorialEnvios repositorioHistorialEnvios = new RepositorioHistorialEnvios(databaseManager);

                RepositorioConfiguracionCorreo repositorioConfiguracionCorreo = new RepositorioConfiguracionCorreo(
                                databaseManager);

                CifradorCredenciales cifradorCredenciales = new CifradorCredenciales();

                ProtectorClave protectorClave = new ProtectorClaveWindowsDpapi();

                ServicioCredencialesCorreo servicioCredencialesCorreo = new ServicioCredencialesCorreo(
                                repositorioConfiguracionCorreo,
                                cifradorCredenciales,
                                protectorClave);

                String codigoDocumento = repositorioConfiguracion.obtenerCodigoDocumento();

                if (codigoDocumento == null) {
                        codigoDocumento = "LIQ";

                        repositorioConfiguracion.guardarCodigoDocumento(codigoDocumento);
                }

                String asuntoCorreo = repositorioConfiguracion.obtenerAsuntoCorreo();

                if (asuntoCorreo == null) {
                        repositorioConfiguracion.guardarAsuntoCorreo("Documentacion");
                }

                String cuerpoCorreo = repositorioConfiguracion.obtenerCuerpoCorreo();

                if (cuerpoCorreo == null) {
                        repositorioConfiguracion.guardarCuerpoCorreo("Se adjunta la documentacion correspondiente.");
                }

                ServicioDocumentos servicioDocumentos = new ServicioDocumentos(repositorioConfiguracion);

                ServicioCorreo servicioCorreo = new ServicioCorreoDiferido(
                                servicioCredencialesCorreo);

                ServicioPreparacionEnvios servicioPreparacionEnvios = new ServicioPreparacionEnvios(
                                servicioDocumentos,
                                repositorioDestinatarios,
                                servicioCorreo,
                                repositorioConfiguracion,
                                repositorioHistorialEnvios);

                try (Scanner scanner = new Scanner(System.in)) {

                        MenuDestinatarios menuDestinatarios = new MenuDestinatarios(
                                        repositorioDestinatarios,
                                        scanner);

                        MenuProcesamientoDocumentos menuProcesamientoDocumentos = new MenuProcesamientoDocumentos(
                                        servicioPreparacionEnvios,
                                        scanner);

                        MenuConfiguracion menuConfiguracion = new MenuConfiguracion(
                                        repositorioConfiguracion,
                                        servicioCredencialesCorreo,
                                        servicioCorreo,
                                        scanner);

                        MenuHistorial menuHistorial = new MenuHistorial(
                                        repositorioHistorialEnvios,
                                        scanner);

                        MenuPrincipal menuPrincipal = new MenuPrincipal(
                                        menuDestinatarios,
                                        menuProcesamientoDocumentos,
                                        menuConfiguracion,
                                        menuHistorial,
                                        scanner);

                        menuPrincipal.mostrar();
                }
        }
}
