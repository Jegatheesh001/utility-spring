package com.myweb.utility.tools.service;

import java.util.Collections;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * @author Jegatheesh<br>
 *         <b>Created</b> On Jan 29, 2019
 *
 */
public class LDAPConnection {
	static DirContext ldapContext;

	public void call() {
		try {
			System.out.println("Connecting Active Directory");

			Hashtable<String, String> ldapEnv = new Hashtable<String, String>(11);
			ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			ldapEnv.put(Context.PROVIDER_URL, "ldap://ldap.forumsys.com:389");
			ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
			// ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=administrateur,cn=users,dc=societe,dc=fr");
			ldapEnv.put(Context.SECURITY_PRINCIPAL, "cn=read-only-admin,dc=example,dc=com");
			ldapEnv.put(Context.SECURITY_CREDENTIALS, "password");
			// ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
			// ldapEnv.put(Context.SECURITY_PROTOCOL, "simple");
			ldapContext = new InitialDirContext(ldapEnv);

			// Create the search controls
			SearchControls searchCtls = new SearchControls();

			// Specify the attributes to return
			// String returnedAtts[] = { "riemann", "gauss", "random" };
			// searchCtls.setReturningAttributes(returnedAtts);

			// Specify the search scope
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// specify the LDAP search filter
			String searchFilter = "(objectClass=*)";

			// Specify the Base for the search
			String searchBase = "dc=example,dc=com";
			// initialize counter to total the results
			int totalResults = 0;

			// Search for objects using the filter
			NamingEnumeration<SearchResult> answer = ldapContext.search(searchBase, searchFilter, searchCtls);

			// Loop through the search results
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();

				totalResults++;

				System.out.println("-----------------------------------------");
				System.out.println(">>>" + sr.getName());
				System.out.println("-----------------------------------------");
				Attributes attrs = sr.getAttributes();
				System.out.println(">>> Attributes ");
				Collections.list(attrs.getAll()).forEach(System.out::println);
				System.out.println(">>>>>>");
			}

			System.out.println("Total results: " + totalResults);
			ldapContext.close();
		} catch (Exception e) {
			System.out.println(" Search error: " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws NamingException {
		new LDAPConnection().call();
	}
}
