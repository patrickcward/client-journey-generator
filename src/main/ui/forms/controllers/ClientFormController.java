package main.ui.forms.controllers;

import main.customers.services.HibernateClientRepository;

public class ClientFormController extends FormController {

    private final HibernateClientRepository clientRepository = new HibernateClientRepository();

}
