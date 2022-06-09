


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/sql/ddlgen/csa_multisite_ddl.xml#1 $$Change: 1385662 $

create table csa_site_configuration (
	id	varchar(40)	not null,
	resource_bundle	varchar(254)	default null,
	large_site_icon	varchar(254)	default null,
	default_country_code	varchar(2)	default null,
	newpass_addr	varchar(254)	default null,
	orderconfirm_addr	varchar(254)	default null,
	changepass_addr	varchar(254)	default null,
	registereduser_addr	varchar(254)	default null
,constraint csa_site_config_p primary key (id));


create table csa_site_languages (
	id	varchar(40)	not null,
	languages	varchar(40)	not null,
	sequence_num	integer	not null
,constraint csa_site_lang_p primary key (id,sequence_num)
,constraint csa_site_lang_f foreign key (id) references site_configuration (id));


create table csa_i18n_site_config (
	id	varchar(40)	not null,
	default_lang	varchar(2)	default null
,constraint csa_i18nsite_pconfig primary key (id)
,constraint csa_i18nsite_fconfig foreign key (id) references site_configuration (id));

commit;


