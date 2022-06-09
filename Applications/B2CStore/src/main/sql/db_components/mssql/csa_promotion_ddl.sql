


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/src/main/sql/ddlgen/csa_promotion_ddl.xml#1 $$Change: 1385662 $

create table csa_prm_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null
,constraint csa_prm_xlate_p primary key (translation_id))


create table csa_prm_prm_xlate (
	promotion_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint csa_prm_prm_xlt_p primary key (promotion_id,locale)
,constraint csa_prm_xlate_f foreign key (translation_id) references csa_prm_xlate (translation_id))

create index csa_prm_xlt_tr_id on csa_prm_prm_xlate (translation_id)


go
