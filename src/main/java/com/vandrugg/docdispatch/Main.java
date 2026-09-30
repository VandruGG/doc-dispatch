package com.vandrugg.docdispatch;

import java.util.Scanner;

import com.vandrugg.docdispatch.config.ConfiguracionCorreoFactory;
import com.vandrugg.docdispatch.database.DatabaseManager;
import com.vandrugg.docdispatch.model.ConfiguracionCorreo;
import com.vandrugg.docdispatch.model.ResultadoConexionCorreo;
import com.vandrugg.docdispatch.repository.RepositorioConfiguracion;
import com.vandrugg.docdispatch.repository.RepositorioDestinatarios;
import com.vandrugg.docdispatch.service.ServicioDocumentos;
import com.vandrugg.docdispatch.service.ServicioCorreo;
import com.vandrugg.docdispatch.service.ServicioCorreoJakartaMail;
import com.vandrugg.docdispatch.service.ServicioPreparacionEnvios;
import com.vandrugg.docdispatch.ui.MenuConfiguracion;
import com.vandrugg.docdispatch.ui.MenuDestinatarios;
import com.vandrugg.docdispatch.ui.MenuPrincipal;
import com.vandrugg.docdispatch.ui.MenuProcesamientoDocumentos;

public class Main {

        public static void main(String[] args) {

                DatabaseManager databaseManager = DatabaseManager.produccion();
                databaseManager.inicializarBaseDeDatos();

                RepositorioDestinatarios repositorioDestinatarios = new RepositorioDestinatarios(databaseManager);

                RepositorioConfiguracion repositorioConfiguracion = new RepositorioConfiguracion(databaseManager);

                String codigoDocumento = repositorioConfiguracion.obtenerCodigoDocumento();

                if (codigoDocumento == null) {
                        codigoDocumento = "LIQ";

                        repositorioConfiguracion.guardarCodigoDocumento(codigoDocumento);
                }

                ServicioDocumentos servicioDocumentos = new ServicioDocumentos(repositorioConfiguracion);

                ConfiguracionCorreo configuracionCorreo = ConfiguracionCorreoFactory.desdeEntorno();

                ServicioCorreo servicioCorreo = new ServicioCorreoJakartaMail(
                                configuracionCorreo);

                ResultadoConexionCorreo resultadoConexion = servicioCorreo.probarConexion();
                
                System.out.println(
                                "Conexion de correo: "
                                                + resultadoConexion.detalle());

                ServicioPreparacionEnvios servicioPreparacionEnvios = new ServicioPreparacionEnvios(
                                servicioDocumentos,
                                repositorioDestinatarios,
                                servicioCorreo);

                try (Scanner scanner = new Scanner(System.in)) {

                        MenuDestinatarios menuDestinatarios = new MenuDestinatarios(
                                        repositorioDestinatarios,
                                        scanner);

                        MenuProcesamientoDocumentos menuProcesamientoDocumentos = new MenuProcesamientoDocumentos(
                                        servicioPreparacionEnvios,
                                        scanner);

                        MenuConfiguracion menuConfiguracion = new MenuConfiguracion(
                                        repositorioConfiguracion,
                                        scanner);

                        MenuPrincipal menuPrincipal = new MenuPrincipal(
                                        menuDestinatarios,
                                        menuProcesamientoDocumentos,
                                        menuConfiguracion,
                                        scanner);

                        menuPrincipal.mostrar();
                }
        }
}
