


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/sql/ddlgen/csa_multisite_ddl.xml#1 $$Change: 1385662 $

create table csa_site_configuration (
	id	varchar2(40)	not null,
	resource_bundle	varchar2(254)	null,
	large_site_icon	varchar2(254)	null,
	default_country_code	varchar2(2)	null,
	newpass_addr	varchar2(254)	null,
	orderconfirm_addr	varchar2(254)	null,
	changepass_addr	varchar2(254)	null,
	registereduser_addr	varchar2(254)	null
,constraint csa_site_config_p primary key (id));


create table csa_site_languages (
	id	varchar2(40)	not null,
	languages	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint csa_site_lang_p primary key (id,sequence_num)
,constraint csa_site_lang_f foreign key (id) references site_configuration (id));


create table csa_i18n_site_config (
	id	varchar2(40)	not null,
	default_lang	varchar2(2)	null
,constraint csa_i18nsite_pconfig primary key (id)
,constraint csa_i18nsite_fconfig foreign key (id) references site_configuration (id));



